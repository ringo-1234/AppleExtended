package jp.ngt.ngtlib.math;

/**複素数*/
public final class Complex
{
	private final double real;
	private final double imaginary;

	public Complex(double r, double i)
	{
		this.real = r;
		this.imaginary = i;
	}

	public Complex add(Complex c)
	{
		return new Complex(this.real + c.real, this.imaginary + c.imaginary);
	}

	public Complex multiply(Complex c)
	{
		double r = this.real * c.real - this.imaginary * c.imaginary;
		double i = this.real * c.imaginary + this.imaginary * c.real;
		return new Complex(r, i);
	}

	public double absSq()
	{
		return this.real * this.real + this.imaginary * this.imaginary;
	}
}