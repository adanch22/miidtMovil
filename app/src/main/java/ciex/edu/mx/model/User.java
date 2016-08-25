package ciex.edu.mx.model;

/**
 * Created by azulyoro on 11/04/16.
 */

import java.io.Serializable;

public class User implements Serializable {
    String id, name, matricula, device_key, created_at ;
    String course_id[], course_level[];

    public User() {}

    public User(String id, String name, String matricula, String device_key, String created_at,
                String[] course_id, String[] course_level) {
        this.id = id;
        this.name = name;
        this.matricula = matricula;
        this.device_key = device_key;
        this.created_at = created_at;
        this.course_id = course_id;
        this.course_level = course_level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String[] getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String[] course_id) {
        this.course_id = course_id;
    }

    public String[] getCourse_level() {
        return course_level;
    }

    public void setCourse_level(String[] course_level) {
        this.course_level = course_level;
    }
}
