package ca.paulshin.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class YTDaoGenerator {
	// http://greendao-orm.com/documentation/relations/
	// http://ismydream.tistory.com/146

	public static void main(String[] args) throws Exception {
		Schema schema = new Schema(1, "ca.paulshin.dao");

		addVideo(schema);

		new DaoGenerator().generateAll(schema, "dao/src/main/java");
	}

	private static void addVideo(Schema schema) {
		Entity note = schema.addEntity("Video");
		note.addIdProperty().autoincrement().primaryKey();
		note.addStringProperty("stitle").notNull();
		note.addStringProperty("ytitle").notNull();
		note.addStringProperty("ytid").notNull();
	}
}
