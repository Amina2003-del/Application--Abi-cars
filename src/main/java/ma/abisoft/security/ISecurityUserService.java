package ma.abisoft.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
