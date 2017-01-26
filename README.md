# Course project 1 based on cybersecuritybase-project template

This project is based on the starter project. The sources can be found at: 
`https://github.com/maeae-vaan/cybersecuritybase-project`

The README.md file contains a formatted version of this text.

This project demonstrates the following vulnerabilities:

- XSS
- CSRF
- SQL injection
- Insecure direct object reference
- Missing function level access control

For testing the following user accounts are precreated in the system:

- bill password:1234 role:USER
- ted password:mom role:USER
- rufus password:admin role:ADMIN

The following functional additions have been made to the starter project:

- Login is required for all pages. Logout is not implemented but can be
  done by clearing cookies.
- Users have a role, either USER or ADMIN, which determines their access.
- USER can fill registration form and view their registration details.
- ADMIN can access a listing of all registrations in addition to the normal
  USER level access.

## Running the application

Start the application with `mvn spring-boot:run`. It is suggested to restart
it after testing each issue to start from a clean slate.

## Identifying the vulnerabilities

### Issue: XSS

This issue is pretty simple to find by simply going though the pages available
to the user trying to insert HTML code such as a script tag into every possible 
input field. In this project the address field can take a script tag which is
then displayed unescaped to the user and also to in the admin view.

Steps to reproduce:

- Visit `http://localhost:8080/`
- Login as user bill
- Fill the form and give address as: `<script>alert("XSS")</script>`
- Clear cookies to logout
- Visit `http://localhost:8080/`
- Login as user rufus
- Observe that the script given in address field is executed

How to fix:

- The page template should use the Thymeleaf command `th:text` instead of `th:utext` 
  to render the address. This would prevent the script from executing as it 
  would be simply displayed as text on the page.

### Issue: CSRF

In general this issue can be found by inspecting input forms in browser developer tools. It
is also possible to inspect the senet data with the browser network inspection tool.
If a form is posted without a CSRF token the application may be vulnerable to CSRF attack.

In this particular case the registration form does not contain a CSRF token and
this can be exploited by tricking a user into a loading some page with an attack
script which would then create a registration using the user's authentication
credentials. The following steps uses another vulnerability on the same website
to store the attack script.

Steps to reproduce:

- Visit `http://localhost:8080/`
- Login as user ted to create the attack script using XSS vuln
- Create a registration with the following typed into the address field: 
`<form action="/form" method="POST" id="f"><input type="hidden" name="name" value="Dummy"><input type="hidden" name="address" value="Korvatunturi"></form><script type="text/javascript">document.getElementById("f").submit()</script>`
- Get the link for the view registration URL, something like `http://localhost:8080/view/1`
- Clear cookies to logout
- Visit `http://localhost:8080/`
- Login as user bill
- Open the abovementioned link (assume Ted sent it to Bill and tricked him to opening it)
- Now Bill is automatically registered to the event

How to fix:

- Enable CSRF protection in Spring Security configuration and make sure the token 
  is included in forms. With Thymeleaf the token can be injected automatically 
  into forms with proper configuration.

### Issue: SQL Injection

In general this can be found by going through forms where the data is stored
into database and checking if special characters such as single quote can
throw off the application. In this particular case we find that a single
quote entered into the address field causes an internal server error and we
can then try to find a string that would do something worse.

Steps to reproduce:

- Visit `http://localhost:8080/`
- Login as user ted and fill the form a few times normally
- Clear cookies to logout
- Visit `http://localhost:8080/`
- Login as user rufus and check that the form registrations can be seen
- Login as user bill
- Fill the form and give address as: `x'); DELETE FROM signup;--`
- Clear cookies to logout
- Visit `http://localhost:8080/`
- Login as user rufus and observe that all registrations have been deleted

How to fix:

- Either use SignupRepository.save for saving or change SignupDao to use prepared 
  statements with bound parameters. 
- With source code review this fault can be found quite easily by looking
  for string concatenation operators in SQL statements.

### Issue: Insecure Direct Object Reference

This issue can be found by using the web application and monitoring the URL bar
when accessing privileged resources. If the URL is predictable you can then
try to change it by hand to access other resources.

Steps to reproduce:

- Visit `http://localhost:8080/`
- Login as user bill and fill the form
- Clear cookies to logout
- Visit `http://localhost:8080/`
- Login as user ted and fill the form
- After filling the form click the view link
- Observe that the URL for viewing the details is something like `/view/2`
- Change the number in the URL to have a different number (e.g., `/view/1`), 
  and observe that you can see Bill's registration

How to fix:

- Validate in the controller that the user is accessing their own registration


### Issue: Missing Function Level Access Control

This can be found by first using the website with admin privileges and taking
note of the URLs used for pages and other network requests. You can then
try to access them without admin role. In this case we find that the
landing page for admin users can be accessed by a normal user.

Steps to reproduce:

- Visit `http://localhost:8080/`
- Login as user bill
- Change the URL to /signups
- View the page source or open the developer tools
- Observe that you can view all registrations without being an admin

How to fix: 

- Do not show the signups page if user does not have admin role by checking 
  the user's role in the controller.
