package org.hibernate.entity;
import java.util.EnumSet;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.*;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil {
	private static String CONFIG_FILE_LOCATION =  "../hibernate.cfg.xml";
	private static Configuration configuration = new Configuration();
	private static String configFile = CONFIG_FILE_LOCATION;
	
	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	static {
		try {
			Configuration cfg = new Configuration().configure();
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
	        sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}catch(Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static Session getSession() throws HibernateException{
		Session session = (Session) threadLocal.get();
		if(session == null || !session.isOpen()) {
			if(sessionFactory == null) {
				rebuildSessionFactory();
			}
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			threadLocal.set(session);
		}
		return session;
	}
	public static void closeSession() throws HibernateException{
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		if(session != null) {
			session.close();
		}
	}
	public static void rebuildSessionFactory() {
		try {
			configuration.configure(configFile);
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}catch(Exception e) {
			System.err.println("Error Creating SessionFactory");
			e.printStackTrace();
		}
	}
	public static void shutdown() {
		getSessionFactory().close();
	}
	
	
}
