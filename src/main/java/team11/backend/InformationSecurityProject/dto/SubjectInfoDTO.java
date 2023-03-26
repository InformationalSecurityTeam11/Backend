package team11.backend.InformationSecurityProject.dto;

public class SubjectInfoDTO {

    // Mandatory fields
    private String commonName;
    private String countryName;
    private String organizationName;

    // Optional fields
    private String organizationUnit;
    private String localityName;
    private String stateName;
    private String email;


    public SubjectInfoDTO(String commonName, String countryName, String organizationName) {
        this.commonName = commonName;
        this.countryName = countryName;
        this.organizationName = organizationName;
    }

    public SubjectInfoDTO(String commonName, String countryName, String organizationName, String organizationUnit, String localityName, String stateName, String email) {
        this.commonName = commonName;
        this.countryName = countryName;
        this.organizationName = organizationName;
        this.organizationUnit = organizationUnit;
        this.localityName = localityName;
        this.stateName = stateName;
        this.email = email;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
