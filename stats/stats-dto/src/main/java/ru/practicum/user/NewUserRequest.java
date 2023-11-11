package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NewUserRequest {
    @Length(min = 2, max = 250)
    @NotBlank
    String name;

    @Email
    @Length(min = 6, max = 254)
    @NotBlank
    String email;
}
