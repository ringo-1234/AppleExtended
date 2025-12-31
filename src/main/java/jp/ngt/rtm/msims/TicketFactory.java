package jp.ngt.rtm.msims;

public class TicketFactory
{
	/**切符の名前*/
	private String name;
	/**切符の種類*/
	private TicketType type;
	/**営業キロ(単位km)*/
	private float[] distance;
	/**運賃, 定額の場合は長さ1配列*/
	private int[] fare;
	/**経由路線, 乗り放題なら乗車可能路線*/
	private String[] routes;
}
