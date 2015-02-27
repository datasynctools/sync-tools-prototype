package tools.datasync.core.sampleapp;


public class ContactMetamodelDaoTests {

    static final String DB_FILE_PATH = "src/test/resources/test1.db";
    //
    // @BeforeClass
    // public static void setupBefore() {
    // FileUtils.deleteQuietly(new File(DB_FILE_PATH));
    // Connection conn = createConnection(DB_FILE_PATH, true);
    // UpdateableDataContext updateableContext = new JdbcDataContext(conn);
    //
    // ContactTableCreator.createDb(updateableContext);
    // SyncStateTableCreator.createDb(updateableContext);
    // }
    //
    // private ContactDao setupTestInsertAndSelect() {
    // Connection conn = createConnection(DB_FILE_PATH, true);
    // UpdateableDataContext updateableContext = new JdbcDataContext(conn);
    //
    // // ContactDao contactDao = new ContactMetamodelDao(updateableContext);
    //
    // SyncRecordFromT<Contact> syncRecordCreator = new SyncRecordFromContact(
    // new DefaultHashFromObject());
    //
    // // ContactDao contactMetamodelDao = new ContactMetamodelDao(
    // // updateableContext, syncRecordCreator);
    // // SyncStateDao syncStateDao = EasyMock.createMock(SyncStateDao.class);
    //
    // // ContactDao contactDao = new ContactSyncDao(contactMetamodelDao,
    // // syncStateDao, syncRecordCreator);
    //
    // ContactDao contactDao = new ContactMetamodelDao(updateableContext,
    // syncRecordCreator);
    //
    // Assert.assertEquals(0, contactDao.getContacts().size());
    //
    // return contactDao;
    // }
    //
    // Contact createTestContact(String contactId) {
    // Contact item = new Contact();
    // item.setContactId(contactId);
    // item.setFirstName("John");
    // item.setLastName(null);
    // item.setHeighInch(9);
    // item.setHeightFt(5);
    // return (item);
    // }
    //
    // @Test
    // public void testInsertAndSelect() {
    //
    // ContactDao contactDao = setupTestInsertAndSelect();
    //
    // String contactId = UUID.randomUUID().toString();
    // Contact item = createTestContact(contactId);
    //
    // contactDao.addContact(item);
    //
    // List<Contact> contacts = contactDao.getContacts();
    // Assert.assertEquals(1, contactDao.getContacts().size());
    // Contact result = contacts.get(0);
    //
    // verifyContactInsertAndSelect(result, contacts, contactId);
    //
    // // SyncStateDao syncStateDao = null;
    //
    // }
    //
    // private void verifyContactInsertAndSelect(Contact result,
    // List<Contact> contacts, String contactId) {
    //
    // Assert.assertEquals(1, contacts.size());
    // Assert.assertEquals(contactId, result.getContactId());
    // Assert.assertEquals("John", result.getFirstName());
    // Assert.assertEquals(null, result.getLastName());
    // Assert.assertEquals(5, result.getHeightFt());
    // Assert.assertEquals(9, result.getHeightInch());
    // }
    //
    // private static void cleanupDir() {
    //
    // File tmpDir = new File(".", "derby-tmp");
    // try {
    // FileUtils.forceMkdir(tmpDir);
    // } catch (IOException e) {
    // e.printStackTrace();
    // throw (new RuntimeException(
    // "Cannot create temp directory for derby"));
    // }
    // }
    //
    // private static void setupDerbyProps() {
    // // doc: http://docs.oracle.com/javadb/10.10.1.2/ref/rrefproper46141.html
    // System.setProperty("derby.locks.waitTimeout", "5");
    // System.setProperty("derby.locks.deadlockTimeout", "4");
    //
    // }
    //
    // private static Connection createConnection(String dbName, boolean create)
    // {
    //
    // cleanupDir();
    //
    // setupDerbyProps();
    //
    // try {
    // Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    // } catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // throw (new RuntimeException("Could not load derby driver"));
    // }
    //
    // Connection conn = null;
    // try {
    // // jdbc:derby:derby/TrustyTask;create=true
    // conn = DriverManager.getConnection("jdbc:derby:" + dbName
    // + ";create=true");
    // } catch (SQLException e) {
    // Utils.closeQuietly(conn);
    // throw (new RuntimeException(e));
    // }
    // return (conn);
    // }

}
