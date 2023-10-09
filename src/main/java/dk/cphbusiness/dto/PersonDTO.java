package dk.cphbusiness.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonDTO {
    String firstName;
    String lastName;
    int yearOfBirth;

}
