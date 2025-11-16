package ma.abisoft.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import ma.abisoft.persistence.model.PasswordResetToken;
import ma.abisoft.persistence.model.User;
import ma.abisoft.persistence.model.VerificationToken;
import ma.abisoft.web.dto.UserDto;
import ma.abisoft.web.error.UserAlreadyExistException;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    String generateQRUrl(User user) throws UnsupportedEncodingException;

    User updateUser2FA(boolean use2FA);

    List<String> getUsersFromSessionRegistry();

	User findByEmail(String email);

	VerificationToken getVerificationTokenForUser(User registered);

	User getCurrentUser();

	boolean resetPassword(String oldPassword, String newPassword);
	
	

}
