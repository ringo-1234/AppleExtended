package jp.ngt.rtm.block.tt;

public class TrainTimeTable
{
	public final TimeTable timeTable;
	public final String train;

	/**-1ならこのオブジェクトはダミー*/
	public final int colIndex;

	public TrainTimeTable(String pTrain)
	{
		this.timeTable = TimeTableManager.INSTANCE.getTimeTableByTrainName(pTrain);
		this.train = pTrain;

		Integer i = this.timeTable.trainAxis.get(this.train);
		this.colIndex = i == null ? -1 : i;
	}
}
