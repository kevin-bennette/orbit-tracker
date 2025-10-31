package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Newtonian physics utilities for orbital mechanics and stellar dynamics.
 *
 * Units (unless stated otherwise):
 * - Distances: Astronomical Units (AU)
 * - Time: years
 * - Mass: Solar masses (Msun)
 * - Velocity: AU/year
 * - Gravitational constant: G = 39.47841760435743 AU^3 / (Msun * yr^2)
 */
@Service
public class NewtonianPhysics {

	// Gravitational constant in AU^3 / (Msun * yr^2)
	public static final double G_AU3_MSUN_YR2 = 39.47841760435743; // 4*pi^2

	/** Simple 3D vector container. */
	public static class Vec3 {
		public final double x;
		public final double y;
		public final double z;

		public Vec3(double x, double y, double z) {
			this.x = x; this.y = y; this.z = z;
		}

		public Vec3 add(Vec3 o) { return new Vec3(x + o.x, y + o.y, z + o.z); }
		public Vec3 sub(Vec3 o) { return new Vec3(x - o.x, y - o.y, z - o.z); }
		public Vec3 mul(double s) { return new Vec3(x * s, y * s, z * s); }
		public double dot(Vec3 o) { return x * o.x + y * o.y + z * o.z; }
		public double norm() { return Math.sqrt(x * x + y * y + z * z); }
	}

	/** Two-body Kepler propagation using universal variable formulation (elliptic only, e < 1). */
	public StateVector propagateKeplerElliptic(StateVector state0, double mu, double dtYears) {
		// mu = G * (m1 + m2) in AU^3/yr^2
		Vec3 r0 = state0.position;
		Vec3 v0 = state0.velocity;
		double r0n = r0.norm();
		double alpha = 2.0 / r0n - v0.dot(v0) / mu; // reciprocal of semi-major axis (1/a)

		// Stumpff functions via series for small arguments
		final int maxIter = 50;
		final double tol = 1e-12;
		double chi = Math.sqrt(mu) * Math.abs(alpha) * dtYears; // initial guess
		if (chi == 0.0) chi = 1e-8;

		for (int k = 0; k < maxIter; k++) {
			double z = alpha * chi * chi;
			double C = stumpffC(z);
			double S = stumpffS(z);
			double f = r0n * v0.dot(v0) / Math.sqrt(mu) * chi * C + (1.0 - alpha * r0n) * chi * chi * chi * S + r0n * chi - Math.sqrt(mu) * dtYears;
			double fp = r0n * v0.dot(v0) / Math.sqrt(mu) * (1.0 - z * S) + (1.0 - alpha * r0n) * chi * chi * C + r0n;
			double dChi = f / fp;
			chi -= dChi;
			if (Math.abs(dChi) < tol) break;
		}

		double z = alpha * chi * chi;
		double C = stumpffC(z);
		double S = stumpffS(z);
		double f = 1.0 - chi * chi * C / r0n;
		double g = dtYears - chi * chi * chi * S / Math.sqrt(mu);
		Vec3 r = r0.mul(f).add(v0.mul(g));
		double rn = r.norm();
		double fdot = (Math.sqrt(mu) / (rn * r0n)) * (z * S - 1.0) * chi;
		double gdot = 1.0 - chi * chi * C / rn;
		Vec3 v = r0.mul(fdot).add(v0.mul(gdot));
		return new StateVector(r, v);
	}

	/** Stumpff C(z). */
	private static double stumpffC(double z) {
		if (Math.abs(z) < 1e-8) return 1.0/2.0 - z/24.0 + z*z/720.0;
		if (z > 0) return (1.0 - Math.cos(Math.sqrt(z))) / z;
		return (1.0 - Math.cosh(Math.sqrt(-z))) / z;
	}

	/** Stumpff S(z). */
	private static double stumpffS(double z) {
		if (Math.abs(z) < 1e-8) return 1.0/6.0 - z/120.0 + z*z/5040.0;
		if (z > 0) return (Math.sqrt(z) - Math.sin(Math.sqrt(z))) / (Math.pow(z, 1.5));
		double sz = Math.sqrt(-z);
		return (Math.sinh(sz) - sz) / (Math.pow(-z, 1.5));
	}

	/** State vector for an object: position (AU) and velocity (AU/yr). */
	public static class StateVector {
		public final Vec3 position;
		public final Vec3 velocity;
		public StateVector(Vec3 position, Vec3 velocity) {
			this.position = position;
			this.velocity = velocity;
		}
	}

	/** Compute gravitational acceleration on body i due to body j. */
	public Vec3 gravitationalAcceleration(Vec3 ri, Vec3 rj, double mj) {
		Vec3 dr = rj.sub(ri);
		double r = dr.norm();
		if (r < 1e-9) return new Vec3(0,0,0);
		double a = G_AU3_MSUN_YR2 * mj / (r * r * r);
		return dr.mul(a);
	}

	/** Simple symplectic (leapfrog) integrator for N-body with fixed timestep. */
	public List<StateVector> integrateNBody(List<StateVector> initial, List<Double> massesMsun, double dtYears, int steps) {
		int n = initial.size();
		List<Vec3> r = new ArrayList<>(n);
		List<Vec3> v = new ArrayList<>(n);
		for (StateVector s : initial) { r.add(s.position); v.add(s.velocity); }

		for (int k = 0; k < steps; k++) {
			// half kick
			List<Vec3> a = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				Vec3 ai = new Vec3(0,0,0);
				for (int j = 0; j < n; j++) if (i != j) {
					ai = ai.add(gravitationalAcceleration(r.get(i), r.get(j), massesMsun.get(j)));
				}
				a.add(ai);
			}
			for (int i = 0; i < n; i++) v.set(i, v.get(i).add(a.get(i).mul(0.5 * dtYears)));

			// drift
			for (int i = 0; i < n; i++) r.set(i, r.get(i).add(v.get(i).mul(dtYears)));

			// half kick
			List<Vec3> a2 = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				Vec3 ai = new Vec3(0,0,0);
				for (int j = 0; j < n; j++) if (i != j) {
					ai = ai.add(gravitationalAcceleration(r.get(i), r.get(j), massesMsun.get(j)));
				}
				a2.add(ai);
			}
			for (int i = 0; i < n; i++) v.set(i, v.get(i).add(a2.get(i).mul(0.5 * dtYears)));
		}

		List<StateVector> out = new ArrayList<>(n);
		for (int i = 0; i < n; i++) out.add(new StateVector(r.get(i), v.get(i)));
		return out;
	}

	/**
	 * Derive orbital elements (a, e) from a state vector around a central mass.
	 * Returns array [a_AU, e].
	 */
	public double[] deriveElements(StateVector sv, double mu) {
		Vec3 r = sv.position; Vec3 v = sv.velocity;
		double rn = r.norm();
		double vn = v.norm();
		double energy = 0.5 * vn * vn - mu / rn;
		double a = - mu / (2.0 * energy);
		Vec3 evec = (v.mul(vn * vn - mu / rn).sub(r.mul(r.dot(v) * 2.0))).mul(1.0 / mu); // not exact; simplified
		double e = clamp(Math.sqrt(evec.dot(evec)), 0.0, 0.999999);
		return new double[]{ a, e };
	}

	private static Vec3 cross(Vec3 a, Vec3 b) {
		return new Vec3(
			a.y * b.z - a.z * b.y,
			a.z * b.x - a.x * b.z,
			a.x * b.y - a.y * b.x
		);
	}

	private static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }
}


