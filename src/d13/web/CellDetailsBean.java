package d13.web;


public class CellDetailsBean {

    private String name;
    private String desc;
    private String people;
    private String hideFull;
    private String mandatory;
    private String hidden;
    private String parent;
    private String newcatName;
    private String newcatParent;
    
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public String getPeople() {
        return people;
    }
    public String getHideFull() {
        return hideFull;
    }
    public String getMandatory() {
        return mandatory;
    }
    public String getHidden() {
        return hidden;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setPeople(String people) {
        this.people = people;
    }
    public void setHideFull(String hideFull) {
        this.hideFull = hideFull;
    }
    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }
    public void setHidden(String hidden) {
        this.hidden = hidden;
    }
    public String getParent() {
        return parent;
    }
    public String getNewcatName() {
        return newcatName;
    }
    public String getNewcatParent() {
        return newcatParent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public void setNewcatName(String newcatName) {
        this.newcatName = newcatName;
    }
    public void setNewcatParent(String newcatParent) {
        this.newcatParent = newcatParent;
    }
    
}
