package jp.ngt.ngtlib.renderer.model;

public final class FaceWithIndex extends Face
{
	public final int[] vtxIndexes;
	public final int[] uvIndexes;

	public FaceWithIndex(int size, int material)
	{
		super(size, material);

		this.vtxIndexes = new int[size];
		this.uvIndexes = new int[size];
	}
}