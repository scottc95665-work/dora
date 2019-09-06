package gov.ca.cwds.inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.db2.jcc.DB2DatabaseMetaData;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.rest.filters.AbstractShiroTest;
import gov.ca.cwds.rest.filters.RequestExecutionContext;
import gov.ca.cwds.rest.filters.RequestExecutionContextImplTest;

/**
 * <a href="http://phineasandferb.wikia.com/wiki/Heinz_Doofenshmirtz">Heinz Doofenshmirtz</a> is the
 * main villain from Disney's Phineas and Ferb. This wanna-be megalomaniac is melodramatic,
 * clueless, and repeatedly outwitted by Perry. He names his gadgets with the suffix, "-inator".
 * 
 * <p>
 * This test support class offers common mocks and scaffolding for common dependencies, like
 * SessionFactory and Hibernate queries.
 * </p>
 * 
 * @param <T> persistence class type
 * 
 * @author CWDS API Team
 */
public class Boots<T> extends AbstractShiroTest {

  protected static final ObjectMapper MAPPER = ObjectMapperUtils.createObjectMapper();

  public static final String DEFAULT_CLIENT_ID = "Jtq8ab8H3N";
  public static final String DEFAULT_PARTICIPANT_ID = "10";

  public static Validator validator;
  public static SystemCodeCache systemCodeCache;

  public SessionFactoryImplementor sessionFactoryImplementor;
  public org.hibernate.SessionFactory sessionFactory;
  public Session session;
  public EntityManager em;
  public SessionFactoryOptions sfo;
  public Transaction transaction;
  public StandardServiceRegistry reg;
  public ConnectionProvider cp;
  public DB2DatabaseMetaData meta;
  public Connection con;
  public Statement stmt;
  public ResultSet rs;
  public NativeQuery<T> nq;
  public ProcedureCall proc;
  public Settings settings;
  public PreparedStatement prepStmt;

  public Query query;

  public Subject mockSubject;
  public PrincipalCollection principalCollection;
  public RequestExecutionContext ctx;

  @BeforeClass
  public static void setupSuite() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    // Authentication, authorization:
    mockSubject = mock(Subject.class);
    principalCollection = mock(PrincipalCollection.class);

    final List<String> list = new ArrayList<>();
    list.add("msg");
    when(principalCollection.asList()).thenReturn(list);
    when(mockSubject.getPrincipals()).thenReturn(principalCollection);
    setSubject(mockSubject);

    // Request context:
    Subject mockSubject = mock(Subject.class);
    PrincipalCollection principalCollection = mock(PrincipalCollection.class);

    when(principalCollection.asList()).thenReturn(list);
    when(mockSubject.getPrincipals()).thenReturn(principalCollection);
    setSubject(mockSubject);

    RequestExecutionContextImplTest.startRequest();

    ctx = RequestExecutionContext.instance();

    // Hibernate, JDBC:
    sessionFactoryImplementor = mock(SessionFactoryImplementor.class);
    sessionFactory = mock(org.hibernate.SessionFactory.class);
    session = mock(Session.class);
    transaction = mock(Transaction.class);
    sfo = mock(SessionFactoryOptions.class);
    reg = mock(StandardServiceRegistry.class);
    cp = mock(ConnectionProvider.class);
    con = mock(Connection.class);
    rs = mock(ResultSet.class);
    stmt = mock(Statement.class);
    em = mock(EntityManager.class);
    proc = mock(ProcedureCall.class);
    meta = mock(DB2DatabaseMetaData.class);

    when(sfo.getBatchFetchStyle()).thenReturn(BatchFetchStyle.DYNAMIC);
    settings = new Settings(sfo, "CWSNS1", "CWSNS1");

    final Map<String, Object> sessionProperties = new HashMap<>();
    sessionProperties.put("hibernate.default_schema", "CWSNS1");

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(sessionFactory.createEntityManager()).thenReturn(em);
    when(sessionFactory.getSessionFactoryOptions()).thenReturn(sfo);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(sessionFactory.openSession()).thenReturn(session);
    when(sessionFactory.getProperties()).thenReturn(sessionProperties);

    when(sessionFactoryImplementor.getCurrentSession()).thenReturn(session);
    when(sessionFactoryImplementor.openSession()).thenReturn(session);
    when(sessionFactoryImplementor.createEntityManager()).thenReturn(em);
    when(sessionFactoryImplementor.getSessionFactoryOptions()).thenReturn(sfo);
    when(sessionFactoryImplementor.getProperties()).thenReturn(sessionProperties);
    when(sessionFactoryImplementor.getSettings()).thenReturn(settings);

    when(session.getSessionFactory()).thenReturn(sessionFactory);
    when(session.getProperties()).thenReturn(sessionProperties);
    when(session.beginTransaction()).thenReturn(transaction);
    when(session.getTransaction()).thenReturn(transaction);
    when(session.createStoredProcedureCall(any(String.class))).thenReturn(proc);

    Mockito.doAnswer(new Answer<Void>() {

      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        final Object[] args = invocation.getArguments();
        final Work work = (Work) args[0];
        work.execute(con);
        return null;
      }

    }).when(session).doWork(any(Work.class));

    when(sfo.getServiceRegistry()).thenReturn(reg);
    when(reg.getService(ConnectionProvider.class)).thenReturn(cp);
    when(cp.getConnection()).thenReturn(con);
    when(con.createStatement()).thenReturn(stmt);
    when(stmt.executeQuery(any())).thenReturn(rs);

    // Result set:
    when(rs.next()).thenReturn(true).thenReturn(false);
    when(rs.getString(any())).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString(contains("IBMSNAP_OPERATION"))).thenReturn("I");
    when(rs.getString("LIMITED_ACCESS_CODE")).thenReturn("N");
    when(rs.getInt(any())).thenReturn(0);

    final java.util.Date date = new java.util.Date();
    final Timestamp ts = new Timestamp(date.getTime());
    when(rs.getDate(any())).thenReturn(new Date(date.getTime()));
    when(rs.getTimestamp("LIMITED_ACCESS_CODE")).thenReturn(ts);
    when(rs.getTimestamp(any())).thenReturn(ts);

    // Native Query:
    nq = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(nq);
    when(nq.setString(any(String.class), any(String.class))).thenReturn(nq);
    when(nq.setParameter(any(String.class), any(String.class), any(StringType.class)))
        .thenReturn(nq);
    when(nq.setFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(nq.setHibernateFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(nq.setReadOnly(any(Boolean.class))).thenReturn(nq);
    when(nq.setCacheMode(any(CacheMode.class))).thenReturn(nq);
    when(nq.setFetchSize(any(Integer.class))).thenReturn(nq);
    when(nq.setCacheable(any(Boolean.class))).thenReturn(nq);
    when(nq.addEntity(any(Class.class))).thenReturn(nq);

    final ScrollableResults scrollableResults = mock(ScrollableResults.class);
    when(nq.scroll(any(ScrollMode.class))).thenReturn(scrollableResults);

    prepStmt = mock(PreparedStatement.class);
    when(con.prepareStatement(any(String.class))).thenReturn(prepStmt);
    when(con.prepareStatement(any(String.class), any(Integer.class), any(Integer.class)))
        .thenReturn(prepStmt);
    when(con.prepareStatement(any())).thenReturn(prepStmt);
    when(con.prepareStatement(any(String.class), any(int[].class))).thenReturn(prepStmt);

    when(prepStmt.executeQuery()).thenReturn(rs);
    when(prepStmt.executeQuery(any())).thenReturn(rs);
    when(prepStmt.executeUpdate()).thenReturn(1);

    when(transaction.getStatus()).thenReturn(TransactionStatus.MARKED_ROLLBACK);

    when(sfo.getServiceRegistry()).thenReturn(reg);
    when(reg.getService(ConnectionProvider.class)).thenReturn(cp);
    when(cp.getConnection()).thenReturn(con);

    when(con.getMetaData()).thenReturn(meta);
    when(con.createStatement()).thenReturn(stmt);

    when(meta.getDatabaseProductName()).thenReturn("DB2");
    when(stmt.executeQuery(any())).thenReturn(rs);
    when(stmt.executeUpdate(any(String.class))).thenReturn(1);

    when(con.prepareStatement(any(String.class))).thenReturn(prepStmt);
    when(con.prepareStatement(any())).thenReturn(prepStmt);
    when(con.prepareStatement(any(String.class), any(int[].class))).thenReturn(prepStmt);

    when(prepStmt.executeQuery()).thenReturn(rs);
    when(prepStmt.executeQuery(any())).thenReturn(rs);
    when(prepStmt.executeUpdate()).thenReturn(1);

    final Query q = Mockito.mock(Query.class);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.getNamedQuery(any(String.class))).thenReturn(q);
    when(q.list()).thenReturn(new ArrayList<>());

    when(q.setString(any(String.class), any(String.class))).thenReturn(q);
    when(q.setShort(any(Short.class), any(Short.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class), any(StringType.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class))).thenReturn(q);
    when(q.setHibernateFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setReadOnly(any(Boolean.class))).thenReturn(q);
    when(q.setCacheMode(any(CacheMode.class))).thenReturn(q);
    when(q.setFetchSize(any(Integer.class))).thenReturn(q);
    when(q.setCacheable(any(Boolean.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(Timestamp.class), any(TimestampType.class)))
        .thenReturn(q);

    when(transaction.getStatus()).thenReturn(TransactionStatus.ACTIVE);

    final ScrollableResults results = mock(ScrollableResults.class);
    when(q.scroll(any(ScrollMode.class))).thenReturn(results);
    when(results.next()).thenReturn(true).thenReturn(false);
    when(results.get()).thenReturn(new Object[0]);
    query = q;
  }

  protected void bombResultSet() throws SQLException {
    doThrow(SQLException.class).when(con).commit();
    when(prepStmt.executeUpdate()).thenThrow(SQLException.class);
    when(prepStmt.executeQuery()).thenThrow(SQLException.class);
    when(rs.next()).thenThrow(SQLException.class);
    when(rs.getString(any(String.class))).thenThrow(SQLException.class);
    when(rs.getString(any(Integer.class))).thenThrow(SQLException.class);
  }

  /**
   * Pass variable arguments of type T.
   * 
   * @param values any number of T values
   * @return mock Hibernate Query of type T
   */
  @SuppressWarnings("unchecked")
  protected Query<T> queryInator(T... values) {
    final Query<T> q = Mockito.mock(Query.class);
    if (values != null && values.length != 0) {
      final T t = ArrayUtils.toArray(values)[0];
      when(session.get(any(Class.class), any(Serializable.class))).thenReturn(t);
      when(session.get(any(String.class), any(Serializable.class))).thenReturn(t);
      when(session.get(any(String.class), any(Serializable.class), any(LockMode.class)))
          .thenReturn(t);
      when(session.get(any(String.class), any(Serializable.class), any(LockOptions.class)))
          .thenReturn(t);
      when(session.get(any(Class.class), any(Serializable.class), any(LockMode.class)))
          .thenReturn(t);
      when(session.get(any(Class.class), any(Serializable.class), any(LockOptions.class)))
          .thenReturn(t);
    }

    final List<T> list = new ArrayList<>();
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.getNamedQuery(any())).thenReturn(q);
    when(q.list()).thenReturn(list);

    when(q.setHibernateFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setReadOnly(any(Boolean.class))).thenReturn(q);
    when(q.setCacheMode(any(CacheMode.class))).thenReturn(q);
    when(q.setFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setFetchSize(any(Integer.class))).thenReturn(q);
    when(q.setCacheable(any(Boolean.class))).thenReturn(q);

    when(q.setParameter(any(String.class), any(String.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(Long.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(Set.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class), any(StringType.class))).thenReturn(q);
    when(q.setString(any(String.class), any(String.class))).thenReturn(q);

    final ScrollableResults results = mock(ScrollableResults.class);
    when(q.scroll(any(ScrollMode.class))).thenReturn(results);
    when(results.next()).thenReturn(true).thenReturn(false);
    when(results.get()).thenReturn(new Object[0]);

    return q;
  }

}
