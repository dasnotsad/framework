package utils.xml;

public class User extends Base {

    private String userName;

    private String email;

    public User() {}

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.setAge(111);
        this.setSchool("setSchool");
    }

    public String toString() {
        return "User:{userName=" + this.userName + ",email=" + this.email + "}" + super.getAge() + "------" + super.getSchool();
    }

    //Getter and Setter...
}