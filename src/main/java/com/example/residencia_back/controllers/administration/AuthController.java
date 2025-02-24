/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.controllers.administration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.residencia_back.configuration.JwtService;
import com.example.residencia_back.entities.administration.Menu;
import com.example.residencia_back.entities.administration.Session;
import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.helpers.SystemText;
import com.example.residencia_back.helpers.ToolHelper;
import com.example.residencia_back.models.administration.AuthCredentials;
import com.example.residencia_back.models.administration.UserDetail;
import com.example.residencia_back.models.utilities.Response;
import com.example.residencia_back.services.administration.MenuService;
import com.example.residencia_back.services.administration.RecaptchaService;
import com.example.residencia_back.services.administration.SessionService;
import com.example.residencia_back.validator.onCreate;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RecaptchaService recaptchaService;

    @Value("${jwt.sessionLifetime}")
    private Long SessionLifetime;

    @Value("${rsa.private_key_pem}")
    private String privateKeyPem;

    /**
    * Authenticates a user using the provided credentials.
    *
    * @param authCredentials Contains the user's email and password for authentication.
    *                        - password: Password to log in.
    * @return Response indicating whether authentication was successful, including a message and any relevant data.
    */
    @PostMapping("/login")
    public ResponseEntity<Response> authenticateUser(@RequestBody @Validated(onCreate.class) AuthCredentials authCredentials, HttpServletRequest request) {
        try {
            if (!recaptchaService.verifyRecaptcha(authCredentials.getRecaptchaResponse(), false)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, SystemText.Login.VALIDACION_CAPTCHA_FALLO, null));
            }

            try {
                authCredentials.setCorreo(ToolHelper.decrypt(authCredentials.getCorreo(), privateKeyPem));
                authCredentials.setPassword(ToolHelper.decrypt(authCredentials.getPassword(), privateKeyPem));
            } catch (Exception e) {
                authCredentials.setCorreo("");
                authCredentials.setPassword("");
            }
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authCredentials.getCorreo(), authCredentials.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetail usuarioDetail = (UserDetail) authentication.getPrincipal();
            if (authentication.getPrincipal() != null) {
                if (usuarioDetail.getUsuario().getActivo()) {
                    User usuario = usuarioDetail.getUsuario();
                    Timestamp dateNow = ToolHelper.castDateTime(ToolHelper.getCurrentDateTime());
                    Timestamp endDate = new Timestamp(dateNow.getTime() + (SessionLifetime * 1_000));
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    
                    String claimName = usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno();
                    String token = jwtService.getToken(userDetails, claimName);
                    //update sesion
                    sessionService.setUpdateActivo(false, usuario, dateNow);
                    Session session = Session.builder().usuario(usuario).token(token).fecha_inicio(dateNow).fecha_fin(endDate).build();
                    //register session
                    sessionService.save(session);
                    List<Menu> menus = menuService.findByRolIdAndOpcionNivel(usuario.getRol().getId(), 1);
                    List< HashMap<String, Object>> opcionesMenu = new ArrayList<>();
                    for (Menu menu : menus) {
                        
                        List<Menu> subMenus = menuService.findByRolIdAndDepensAndOpcionNivel(usuario.getRol().getId(), menu.getId(), 2);
                    
                        List<HashMap<String, Object>> subMenusList = new ArrayList<>();
                        for (Menu subMenu : subMenus) {
                            List<Menu> subSubMenus = menuService.findByRolIdAndDepensAndOpcionNivel(usuario.getRol().getId(), subMenu.getOpcion().getId(), 3);
                            HashMap<String, Object> subMenuMap = new HashMap<>(subMenu.toMap()); 
                            subMenuMap.put("subMenus", subSubMenus); 
                            subMenusList.add(subMenuMap);
                        }
                        HashMap<String, Object> opt = new HashMap<>();
                        opt.put("menu", menu);
                        opt.put("subMenus", subMenusList);
                                     opcionesMenu.add(opt);
                        }
                    HashMap<String, Object> usuarioMap = new HashMap<>();
                    usuarioMap.put("id", usuario.getId());
                    usuarioMap.put("usuario", usuario.getUsuario());
                    usuarioMap.put("nombre", usuario.getNombre());
                    usuarioMap.put("apellidoPaterno", usuario.getApellidoPaterno());
                    usuarioMap.put("apellidoMaterno", usuario.getApellidoMaterno());
                    usuarioMap.put("rol", usuario.getRol());

                    HashMap<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("usuario", usuarioMap);
                    response.put("menus", opcionesMenu);
					return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
                } else {
					return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, SystemText.Login.USUARIO_INACTIVO, null));
                }
            } else {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, SystemText.Login.CREDENCIALES_INVALIDAS, null));
            }
        } catch (AuthenticationException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, ex.getMessage(), null));
        }
    }

    /**
    * Logs out the specified user from the system.
    *
    * @param usuario Contains the user's details necessary for logging out.
    *                - id: The unique identifier of the user.
    * @return Response indicating whether the logout process was successful.
    */
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestBody User usuario, HttpServletRequest request) {
        Timestamp dateNow = ToolHelper.castDateTime(ToolHelper.getCurrentDateTime());
        //update sesion
        sessionService.setUpdateActivo(false, usuario, dateNow);
		return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }
}
