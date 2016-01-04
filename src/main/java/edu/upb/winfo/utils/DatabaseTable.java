package edu.upb.winfo.utils;

import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import java.sql.Connection;

/**
 * Created by geskill on 04.01.2016.
 *
 * @author geskill
 */
public class DatabaseTable {

	protected static final Logger logger = LoggerFactory.getLogger(DatabaseTable.class);

	protected Connection con;

	public DatabaseTable(Connection connArg) {
		super();
		this.con = connArg;
	}

}
