package com.oroboks;


/**
 * Test case for {@link UserResource}
 * @author Aditya Narain
 */
//@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {
    //    @Mock
    //    private UserDAO userDAO;
    //
    //    @Mock
    //    private LocationDAO locationDAO;
    //
    //    @Mock
    //    private UserLocationDAO userLocationDAO;
    //    static final URI BASE_URI = getBaseURI();
    //    HttpServer server;
    //    private static URI getBaseURI() {
    //	return UriBuilder.fromUri( "http://localhost/" ).port( 9998 ).build();
    //    }
    //
    //    /**
    //     * Setup for starting server
    //     * @throws IOException if an I/O exception occurs
    //     */
    //    @Before
    //    public void startServer() throws IOException{
    //	Injector injector = Guice.createInjector( new ServletModule() {
    //	    @Override
    //	    protected void configureServlets() {
    //		bind(new TypeLiteral<DAO<User>>() {}).to(UserDAO.class);
    //		bind(new TypeLiteral<DAO<UserLocation>>() {}).to(UserLocationDAO.class);
    //		bind(new TypeLiteral<DAO<Location>>() {}).to(LocationDAO.class);
    //	    }
    //	});
    //
    //	ResourceConfig rc = new PackagesResourceConfig( "com.oroboks" );
    //	IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory( rc, injector );
    //	server = GrizzlyServerFactory.createHttpServer( BASE_URI, rc, ioc );
    //    }
    //
    //    /**
    //     *  Teardown to stop server after test is run completely.
    //     */
    //    @After
    //    public void tearDownServer(){
    //	server.stop();
    //    }
    //
    //    /**
    //     * @throws IOException
    //     */
    //    @Test
    //    public void getAllUsers() throws IOException{
    //	Client client = Client.create( new DefaultClientConfig() );
    //	WebResource service = client.resource( getBaseURI() );
    //	ClientResponse reponse = service.path("/users").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
    //	String text = reponse.getEntity(String.class);
    //    }
    //

}
