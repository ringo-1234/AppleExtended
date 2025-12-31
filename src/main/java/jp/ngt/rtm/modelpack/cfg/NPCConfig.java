package jp.ngt.rtm.modelpack.cfg;

import jp.ngt.rtm.entity.npc.EntityNPC;


public class NPCConfig extends ModelConfig
{
	private String name;
	public ModelSource model;
	public String texture;
	public String lightTexture;
	/**役割*/
	public String role;

	public float health;
	public float speed;
	public float damage;

	@Override
	public void init()
	{
		super.init();

		if(this.health <= 0.0F)
		{
			this.health = EntityNPC.MAX_HEALTH;
		}

		if(this.speed <= 0.0F)
		{
			this.speed = EntityNPC.SPEED;
		}

		if(this.damage <= 0.0F)
		{
			this.damage = EntityNPC.ATTACK_POWER;
		}
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public static NPCConfig getDummy()
	{
		NPCConfig cfg = new NPCConfig();
		cfg.name = "dummy";
		return cfg;
	}
}