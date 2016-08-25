package ciex.edu.mx.app;

/**
 * Created by azulyoro on 11/04/16.
 * Here we declare the REST API endpoint urls.
 * use the correct Ip address of the computer/server on which php services are running.
 */
public class EndPoints {
    // localhost url -
    public static final String BASE_URL = "http://www.itnovacion.com/ciex/";
    //public static final String BASE_URL = "http://172.16.1.106/ciex/v1";
    public static final String LOGIN = BASE_URL + "v1/student/login/";
    public static final String AUTHENTICATE = BASE_URL + "v1/student/authenticate/";
    public static final String STUDENT = BASE_URL + "v1/student/_ID_";
    public static final String COURSES = BASE_URL + "v1/courses";
    public static final String CHAT_THREAD = BASE_URL + "v1/course/messages/_ID_/";
    public static final String BOOKS = BASE_URL + "v1/books/";


    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String UNITS_CONTENT_URL="http://www.itnovacion.com/ciex/levels/level?/book?/";
    public static final String EXERCISE_URL="http://www.itnovacion.com/ciex/book/book_?_/_¿_.xml";
    public static final String BOOKS_URL="http://www.itnovacion.com/ciex/levels/level?/book?/";
}
