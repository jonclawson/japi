# JAPI - Java API

Restful API using Spring Boot, Mysql, JPA.

## Setup

**1. Download**

```bash
git clone ...
```

**2. Create Mysql database**
```bash
cd src/main/resources; docker-compose up
```
- import `src/main/resources/japi.sql`

**3. Change mysql username and password as per your installation**

+ open `src/main/resources/application.properties`
+ change `spring.datasource.username` and `spring.datasource.password` as per your mysql installation

**4. Run the app using maven**

```bash
mvn spring-boot:run
```
The app will start running at <http://localhost:8080>


API

### Auth
```
| POST   | /api/auth/signup | Sign up | [JSON](#signup) |
| POST   | /api/auth/signin | Log in | [JSON](#signin) |
```
### Users
```
| GET    | /api/users/me | Get logged in user profile | |
| GET    | /api/users/{username}/profile | Get user profile by username | |
| POST   | /api/users | Add user (Only for admins) | [JSON](#usercreate) |
| PUT    | /api/users/{username} | Update user (If profile belongs to logged in user or logged in user is admin) | [JSON](#userupdate) |
| DELETE | /api/users/{username} | Delete user (For logged in user or admin) | |

| GET    | /api/users/{username}/posts | Get posts created by user | |
```
### Posts
```
| GET    | /api/posts | Get all posts | |
| GET    | /api/posts/{id} | Get post by id | |
| POST   | /api/posts | Create new post (By logged in user) | [JSON](#postcreate) |
| PUT    | /api/posts/{id} | Update post (If post belongs to logged in user or logged in user is admin) | [JSON](#postupdate) |
| DELETE | /api/posts/{id} | Delete post (If post belongs to logged in user or logged in user is admin) | |
```

## Sample Requests

##### Sign Up -> /api/auth/signup</a>
```json
{
	"firstName": "Joe",
	"lastName": "Bang",
	"username": "joebang",
	"password": "password",
	"email": "joe.bang@gmail.com"
}
```

##### Log In -> /api/auth/signin</a>
```json
{
	"usernameOrEmail": "joebang",
	"password": "password"
}
```

##### Create User -> /api/users</a>
```json
{
	"firstName": "Joe",
	"lastName": "Bang",
	"username": "joebang",
	"password": "password",
	"email": "joe.bang@gmail.com",
	"phone": "760.555.5289",
	"website": "http://example.com",
}
```

##### Update User -> /api/users/{username}</a>
```json
{
	"firstName": "Joe",
	"lastName": "Bang",
	"username": "joebang",
	"password": "updatedpassword",
	"email": "joe.bang@gmail.com",
	"phone": "760.555.5289",
	"website": "http://example.com",
}
```

##### <a id="postcreate">Create Post -> /api/posts</a>
```json
{
	"title": "Welcome to the machine",
	"body": "bla bla bla..."
}
```

##### <a id="postupdate">Update Post -> /api/posts/{id}</a>
```json
{
	"title": "Shine on you loco stone",
	"body": "yada yeada yada... "
}
```


