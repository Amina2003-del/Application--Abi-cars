package ma.abisoft.service;

import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import ma.abisoft.persistence.dao.AdresseMacRepository;
import ma.abisoft.persistence.model.AdresseMac;
import ma.abisoft.persistence.model.User;

@Component
public class AdresseMacService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AdresseMacRepository adresseMacRepository;
	
	public AdresseMacService(AdresseMacRepository adresseMacRepository) {
		this.adresseMacRepository = adresseMacRepository;
	}

	public void verifyDevice(User user, HttpServletRequest request) throws IOException, GeoIp2Exception {

        String ip = extractIp(request);
        System.out.println("Adresse Ip: "+ip);
        String mac = getMACAddress(ip);
        System.out.println("Adresse mac: "+mac);
        //String location = getIpLocation(ip);
        AdresseMac existingMac = findExistingMac(user.getId(), mac);

        if (Objects.isNull(existingMac)) {
            AdresseMac adresseMac = new AdresseMac();
            adresseMac.setUserId(user.getId());
            adresseMac.setSupprime(0);
            adresseMac.setMac(mac);
            adresseMac.setLastLoggedIn(new Date());
            adresseMacRepository.save(adresseMac);
        } else {
        	existingMac.setLastLoggedIn(new Date());
        	adresseMacRepository.save(existingMac);
        }

    }
	
	 private AdresseMac findExistingMac(Long userId, String mac) {
	        List<AdresseMac> knownDevices = adresseMacRepository.findByUserId(userId);
	        for (AdresseMac existingMac : knownDevices) {
	            if (existingMac.getMac().equals(mac)) {
	                return existingMac;
	            }
	        }

	        return null;
	    }
	
	private String extractIp(HttpServletRequest request) {
        String clientIp;
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        if (nonNull(clientXForwardedForIp)) {
            clientIp = parseXForwardedHeader(clientXForwardedForIp);
        } else {
            clientIp = request.getRemoteAddr();
           // clientIp = request.getLocalAddr();
        }

        return clientIp;
    }
	private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }
	
	public static String getMACAddress(String ip) {
        String line = "";
        String macAddress = "";
        final String MAC_ADDRESS_PREFIX = "MAC";
        final String LOOPBACK_ADDRESS = "127.0.0.1";
        try {
            if (LOOPBACK_ADDRESS.equals(ip)) {
                InetAddress inetAddress = InetAddress.getLocalHost();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
                byte[] mac = networkInterface.getHardwareAddress();
                if (networkInterface == null || mac == null) {
                    return "127.0.0.1";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    if (i != 0) {
                        sb.append("-");
                    }
                    String s = Integer.toHexString(mac[i] & 0xFF);
                    sb.append(s.length() == 1 ? 0 + s : s);
                }
                  macAddress = sb.toString().trim().toUpperCase();
                return macAddress;
            }

            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line != null) {
                    int index = line.indexOf(MAC_ADDRESS_PREFIX);
                    if (index != -1) {
                        index = line.indexOf("=");
                        macAddress = line.substring(index + 1).trim().toUpperCase();
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            return "";
        }
        return macAddress;
    }
}
