import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.io.IOException;

@SpringBootTest(classes = FilmorateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EndpointsTests {

    @Value("${local.server.port}")
    private Integer port;

    void template(String description, String endpoint, String RequestBody, String ResponseBody, MediaType RequestType, MediaType ResponseType, HttpStatus StatusResponse, HttpMethod method) throws ValidationException {
        String bodyJsonRequest = RequestBody;
        // print to IO
        System.out.println("------------" + description + "--------------");
        System.out.println("---TEST ADDRESS---");
        System.out.println("http://localhost:" + port + "/" + endpoint);
        System.out.println("---  REQUEST AND RESPONSE---");
        // https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
        // Test with WebTest
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        client.method(method).uri(endpoint)
                .header("Content-Type", RequestType.toString())
                .bodyValue(bodyJsonRequest)
                .exchange()
                .expectAll(
                        // check status code
                        spec -> spec.expectStatus().isEqualTo(StatusResponse),
                        // check content-type
                        spec -> spec.expectHeader().contentType(ResponseType),
                        // check body
                        spec -> spec.expectBody().json(ResponseBody),
                        spec -> System.out.println(spec.returnResult(Response.class))
                );
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    void caseTests() throws IOException {
        //----USERS-----
        this.template("User create",
                "users",
                "{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"Nick Name\",\n" +
                        "  \"email\": \"mail@mail.ru\",\n" +
                        "  \"birthday\": \"1946-08-20\"\n" +
                        "}",
                "{\"id\":1,\"email\":\"mail@mail.ru\",\"login\":\"dolore\",\"name\":\"Nick Name\",\"birthday\":\"1946-08-20\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("User create Fail login",
                "users",
                "{\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"email\": \"yandex@mail.ru\",\n" +
                        "  \"birthday\": \"2446-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("User create Fail email",
                "users",
                "{\n" +
                        "  \"login\": \"dolore ullamco\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"mail.ru\",\n" +
                        "  \"birthday\": \"1980-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("User create Fail birthday",
                "users",
                "{\n" +
                        "  \"login\": \"dolore\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"test@mail.ru\",\n" +
                        "  \"birthday\": \"2446-08-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("User update",
                "users",
                "{\n" +
                        "  \"login\": \"doloreUpdate\",\n" +
                        "  \"name\": \"est adipisicing\",\n" +
                        "  \"id\": 1,\n" +
                        "  \"email\": \"mail@yandex.ru\",\n" +
                        "  \"birthday\": \"1976-09-20\"\n" +
                        "}",
                "{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("User update unknown",
                "users",
                "{\n" +
                        "  \"login\": \"doloreUpdate\",\n" +
                        "  \"name\": \"est adipisicing\",\n" +
                        "  \"id\": 9999,\n" +
                        "  \"email\": \"mail@yandex.ru\",\n" +
                        "  \"birthday\": \"1976-09-20\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );
        this.template("User get All",
                "users",
                "",
                "[{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FRIENDS1-----
        this.template("Friend Create",
                "users",
                "{\n" +
                        "  \"login\": \"friend\",\n" +
                        "  \"name\": \"friend adipisicing\",\n" +
                        "  \"email\": \"friend@mail.ru\",\n" +
                        "  \"birthday\": \"1976-08-20\"\n" +
                        "}",
                "{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Common Friend Create",
                "users",
                "{\n" +
                        "  \"login\": \"common\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"email\": \"friend@common.ru\",\n" +
                        "  \"birthday\": \"2000-08-20\"\n" +
                        "}",
                "{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("User get by id=1",
                "users/1",
                "",
                "{\"id\":1,\"email\":\"mail@yandex.ru\",\"login\":\"doloreUpdate\",\"name\":\"est adipisicing\",\"birthday\":\"1976-09-20\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User get unknown with id=9999",
                "users/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Friend get user id=2",
                "users/2",
                "",
                "{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FRIENDS2-----
        this.template("User get friends common empty",
                "users/1/friends/common/2",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=1 add friend id=2",
                "users/1/friends/2",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("User id=1 add friend unknown id=-1",
                "users/1/friends/-1",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );
        this.template("User id=1 get friends",
                "users/1/friends",
                "",
                "[{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=2 get friends. Not confirm",
                "users/2/friends",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Empty Common friends to user id=1 with user id=2",
                "users/1/friends/common/2",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=1 add  friend id=3",
                "users/1/friends/3",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("User id=1 get 2 friends",
                "users/1/friends",
                "",
                "[{\"id\":2,\"email\":\"friend@mail.ru\",\"login\":\"friend\",\"name\":\"friend adipisicing\",\"birthday\":\"1976-08-20\"},{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=2 add  friend id=3",
                "users/2/friends/3",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("User id=2 get 1 friends",
                "users/2/friends",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Common friend to user id=1 with user id=2",
                "users/1/friends/common/2",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=1 remove friend id=2",
                "users/1/friends/2",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.DELETE
        );
        this.template("User id=1 get common with user id=2",
                "users/1/friends/common/2",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=2 get common with user id=1",
                "users/2/friends/common/1",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("User id=1 get 1 friend",
                "users/1/friends",
                "",
                "[{\"id\":3,\"email\":\"friend@common.ru\",\"login\":\"common\",\"name\":\"common\",\"birthday\":\"2000-08-20\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FILMS-----
        this.template("Film get All",
                "films",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 create",
                "films",
                "{\n" +
                        "  \"name\": \"nisi eiusmod\",\n" +
                        "  \"description\": \"adipisicing\",\n" +
                        "  \"releaseDate\": \"1967-03-25\",\n" +
                        "  \"duration\": 100,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{\"id\":1,\"name\":\"nisi eiusmod\",\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100,\"genres\":[],\"mpa\":{\"id\":1,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Film create Fail name",
                "films",
                "{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"description\": \"Description\",\n" +
                        "  \"releaseDate\": \"1900-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.BAD_REQUEST,
                HttpMethod.POST
        );
        this.template("Film create Fail description",
                "films",
                "{\n" +
                        "  \"name\": \"Film name\",\n" +
                        "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                        "    \"releaseDate\": \"1900-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("Film create Fail releaseDate",
                "films",
                "{\n" +
                        "  \"name\": \"Name\",\n" +
                        "  \"description\": \"Description\",\n" +
                        "  \"releaseDate\": \"1890-03-25\",\n" +
                        "  \"duration\": 200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("Film create Fail duration",
                "films",
                "{\n" +
                        "  \"name\": \"Name\",\n" +
                        "  \"description\": \"Descrition\",\n" +
                        "  \"releaseDate\": \"1980-03-25\",\n" +
                        "  \"duration\": -200,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("Film update",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 2}\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":2,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film update unknown",
                "films",
                "{\n" +
                        "  \"id\": 9999,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"mpa\": { \"id\": 1}\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );
        this.template("Film get All",
                "films",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film get Popular",
                "films/popular",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----FILM-----
        this.template("Film id=2 create",
                "films",
                "{\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}]\n" +
                        "}",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Film id=1 get",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=9999 get not found",
                "films/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        //----Like-----
        this.template("Film id=2 add Like from user id=1",
                "films/2/like/1",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film most popular film",
                "films/popular?count=1",
                "",
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=2 add Like from user id=1",
                "films/2/like/1",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.DELETE
        );
        this.template("Film get all popular film",
                "films/popular",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":2,\"name\":\"PG\"},\"directors\":[]},{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=2 remove Like from user id=-2  not found",
                "films/2/like/-2",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.DELETE
        );
        //----MPA-----
        this.template("Mpa id=1 get",
                "mpa/1",
                "",
                "{\"id\":1,\"name\":\"G\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Mpa  id=9999 get not found",
                "mpa/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Mpa  get All",
                "mpa",
                "",
                "[{\"id\":1,\"name\":\"G\"},{\"id\":2,\"name\":\"PG\"},{\"id\":3,\"name\":\"PG-13\"},{\"id\":4,\"name\":\"R\"},{\"id\":5,\"name\":\"NC-17\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        //----GENRE-----
        this.template("Genre id=1 get",
                "genres/1",
                "",
                "{\"id\":1,\"name\":\"Комедия\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Genre get unknown",
                "genres/9999",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Genre All",
                "genres",
                "",
                "[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"},{\"id\":3,\"name\":\"Мультфильм\"},{\"id\":4,\"name\":\"Триллер\"},{\"id\":5,\"name\":\"Документальный\"},{\"id\":6,\"name\":\"Боевик\"}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 update genre",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"genres\": [{ \"id\": 2}]\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":5,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film id=1 get with genre",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film All with genre",
                "films",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[]},{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 update remove  genre",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"genres\": []\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film id=1 get without genre",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=2 genres update",
                "films",
                "{\n" +
                        "  \"id\": 2,\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 3}]\n" +
                        "}\n",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":3,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film id=2  get with genres",
                "films/2",
                "",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"},{\"id\":3,\"name\":\"Мультфильм\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=2  genres update with duplicate",
                "films",
                "{\n" +
                        "  \"id\": 2,\n" +
                        "  \"name\": \"New film\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"New film about friends\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}, { \"id\": 2}, { \"id\": 1}]\n" +
                        "}\n",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film id=2  get with genre  without duplicate",
                "films/2",
                "",
                "{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );


        //add-most-popular
        this.template("Film get Popular with genre id=1",
                "films/popular?genreId=1",
                "",
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with genre id=2",
                "films/popular?genreId=2",
                "",
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with genre id=3 not exist",
                "films/popular?genreId=3",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with year=1999",
                "films/popular?year=1999",
                "",
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with year=2000 not exits",
                "films/popular?year=2000",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with genre id=1 and year=1999",
                "films/popular?year=1999&genreId=1",
                "",
                "[{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        this.template("Film get Popular with genre id=2 and year=2000 not exits",
                "films/popular?year=2000&genreId=2",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );

        //----DIRECTOR-----

        this.template("Get all directors before create",
                "directors",
                "",
                "[]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Get director 1 before create",
                "directors/1",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Create Director id=1",
                "directors",
                "{\n" +
                        "  \"id\": 10,\n" +
                        "  \"name\": \"Director\"\n" +
                        "}",
                "{\"id\":1,\"name\":\"Director\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Get director id=1 after create",
                "directors/1",
                "",
                "{\"id\":1,\"name\":\"Director\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Create director Fail name",
                "directors",
                "{\n" +
                        "  \"id\": 10,\n" +
                        "  \"name\": \" \"\n" +
                        "}",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpMethod.POST
        );
        this.template("Update Director id=1",
                "directors",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Director updated\"\n" +
                        "}",
                "{\"id\":1,\"name\":\"Director updated\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Update unknown director id=10",
                "directors",
                "{\n" +
                        "  \"id\": 10,\n" +
                        "  \"name\": \"Director updated\"\n" +
                        "}",
                "{}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.PUT
        );
        this.template("Get unknown director 10",
                "directors/10",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Get all directors",
                "directors",
                "",
                "[{\"id\":1,\"name\":\"Director updated\"}]",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 add director id=1",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"2089-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"directors\": [{ \"id\": 1}]\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"2089-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":null},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Film id=3  create with director id=1",
                "films",
                "{\n" +
                        "  \"name\": \"New film with director\",\n" +
                        "  \"releaseDate\": \"1999-04-30\",\n" +
                        "  \"description\": \"Film with director\",\n" +
                        "  \"duration\": 120,\n" +
                        "  \"mpa\": { \"id\": 3},\n" +
                        "  \"genres\": [{ \"id\": 1}],\n" +
                        "  \"directors\": [{ \"id\": 1}]\n" +
                        "}",
                "{\"id\":3,\"name\":\"New film with director\",\"description\":\"Film with director\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":null},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Get all films with directors",
                "films",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"2089-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]},{\"id\":2,\"name\":\"New film\",\"description\":\"New film about friends\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"},{\"id\":2,\"name\":\"Драма\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[]},{\"id\":3,\"name\":\"New film with director\",\"description\":\"Film with director\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Get films with director id=1 sort by year",
                "films/director/1?sortBy=year",
                "",
                "[{\"id\":3,\"name\":\"New film with director\",\"description\":\"Film with director\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]},{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"2089-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Get films with director id=1 sort by likes",
                "films/director/1?sortBy=likes",
                "",
                "[{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"2089-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]},{\"id\":3,\"name\":\"New film with director\",\"description\":\"Film with director\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}]",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Get film id=3 with director",
                "films/3",
                "",
                "{\"id\":3,\"name\":\"New film with director\",\"description\":\"Film with director\",\"releaseDate\":\"1999-04-30\",\"duration\":120,\"genres\":[{\"id\":1,\"name\":\"Комедия\"}],\"mpa\":{\"id\":3,\"name\":\"PG-13\"},\"directors\":[{\"id\":1,\"name\":\"Director updated\"}]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 update remove director",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5}\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":null},\"directors\":[]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Get film id=1 without director",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Create temp Director id=2",
                "directors",
                "{\n" +
                        "  \"id\": 11,\n" +
                        "  \"name\": \"Temp Director\"\n" +
                        "}",
                "{\"id\":2,\"name\":\"Temp Director\"}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.POST
        );
        this.template("Get director id=2 after create",
                "directors/2",
                "",
                "{\"id\":2,\"name\":\"Temp Director\"}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Film id=1 add temp director id=2",
                "films",
                "{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Film Updated\",\n" +
                        "  \"releaseDate\": \"1989-04-17\",\n" +
                        "  \"description\": \"New film update decription\",\n" +
                        "  \"duration\": 190,\n" +
                        "  \"rate\": 4,\n" +
                        "  \"mpa\": { \"id\": 5},\n" +
                        "  \"directors\": [{ \"id\": 2}]\n" +
                        "}",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":null},\"directors\":[{\"id\":2,\"name\":\"Temp Director\"}]}",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.PUT
        );
        this.template("Get film id=1 with temp director id=2",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[{\"id\":2,\"name\":\"Temp Director\"}]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Delete director id=2",
                "directors/2",
                "",
                "",
                MediaType.ALL,
                null,
                HttpStatus.OK,
                HttpMethod.DELETE
        );
        this.template("Get director id=2 after delete",
                "directors/2",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
        this.template("Get film id=1 with deleted director",
                "films/1",
                "",
                "{\"id\":1,\"name\":\"Film Updated\",\"description\":\"New film update decription\",\"releaseDate\":\"1989-04-17\",\"duration\":190,\"genres\":[],\"mpa\":{\"id\":5,\"name\":\"NC-17\"},\"directors\":[]}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.OK,
                HttpMethod.GET
        );
        this.template("Get films with deleted director",
                "films/director/2?sortBy=likes",
                "",
                "{}",
                MediaType.ALL,
                MediaType.APPLICATION_JSON,
                HttpStatus.NOT_FOUND,
                HttpMethod.GET
        );
    }
}
