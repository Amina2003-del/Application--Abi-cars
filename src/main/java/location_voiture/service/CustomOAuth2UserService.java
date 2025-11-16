package location_voiture.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import ma.abisoft.persistence.dao.RoleRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.Role;
import ma.abisoft.persistence.model.User;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);

            Role clientRole = roleRepository.findByName("ROLE_CLIENT");
            if (clientRole == null) {
                throw new RuntimeException("Rôle CLIENT non trouvé");
            }

            user.setRoles(List.of(clientRole));
            userRepository.save(user);
        }

        // Préparer la collection des GrantedAuthority à partir des rôles de User
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
        	    // PAS de "ROLE_" ajouté ici car déjà présent en base
        	    .map(role -> new SimpleGrantedAuthority(role.getName()))
        	    .collect(Collectors.toList());


        // Retourner un DefaultOAuth2User avec ces autorités
        return new DefaultOAuth2User(
            authorities,
            oAuth2User.getAttributes(),
            "email"  // l'attribut principal utilisé comme username
        );
}
}


