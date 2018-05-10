package org.hibernate.entity;
import java.util.EnumSet;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.*;
import org.hibernate.service.ServiceRegistry;

//version : Hibernate 5.5

public class HibernateUtil {
	private static String CONFIG_FILE_LOCATION =  "../hibernate.cfg.xml";
	private static Configuration configuration = new Configuration();
	private static String configFile = CONFIG_FILE_LOCATION;
	
	private static SessionFactory sessionFactory;
	//创建线程局部变量 threadLocal, 用来保存Hibernate的Session
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	//使用静态代码初始化Hibernate
	static {
		try {
			Configuration cfg = new Configuration().configure();		//创建Configuration的实例
			//创建SessionFactory
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
	        sessionFactory = cfg.buildSessionFactory(serviceRegistry);
		}catch(Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	//获得SessinoFactory的实例
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	//获得ThreadLocal对象管理的Session实例
	public static Session getSession() throws HibernateException{
		Session session = (Session) threadLocal.get();
		if(session == null || !session.isOpen()) {
			if(sessionFactory == null) {
				rebuildSessionFactory();
			}
			//通过SessionFactory对象创建Session对象
			session = (sessionFactory != null) ? sessionFactory.openSession() : null;
			//将新打开的Session实例保存到线程局部变量threadLocal中
			threadLocal.set(session);
		}
		return session;
	}
	//关闭Session实例
	public static void closeSession() throws HibernateException{
		//从线程局部变量threadLocal中获取之前存入的Session实例
		Session session = (Session) threadLocal.get();
		threadLocal.set(null);
		if(session != null) {
			session.close();
		}
	}
	//重建SessionFactory
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
	//关闭缓存和连接池
	public static void shutdown() {
		getSessionFactory().close();
	}
	
	
}
