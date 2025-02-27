/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.helpers;

public class SystemText {

    // Módulo: General
    public static class General {

        public static final String PROCESO_EXITOSO = "Proceso realizado exitosamente";
        public static final String REGISTRO_CREADO_CORRECTAMENTE = "Registro creado correctamente";
        public static final String REGISTRO_ACTUALIZADO_CORRECTAMENTE = "Registro actualizado correctamente";
        public static final String REGISTRO_ELIMINADO_CORRECTAMENTE = "Registro eliminado correctamente";
        public static final String REGISTRO_ENVIADO_CORRECTAMENTE = "Registro enviado correctamente";
        public static final String ERROR_PROCESO = "Ocurrio un error en el proceso";
        public static final String FALTA_INFORMACION = "Falta agregar más información";
        public static final String INFORMACION_INCORRECTA = "Información incorrecta";
        public static final String REGISTRO_ENCONTRADO = "Registro encontrado";
        public static final String REGISTRO_NO_ENCONTRADO = "Registro no encontrado";
        public static final String NO_HAY_REGISTROS = "No hay registros para mostrar";
        public static final String NO_ELIMINAR_REGISTRO_ASOCIADO = "No se puede eliminar el registro porque esta asociada a otra información.";
        public static final String NO_GUARDADO_DOCUMENTO = "No se pudo gurdar el archivo.";

        public static final String CAMPO_INVALIDO_SORT(String texto) {
            return "La clave '" + texto + "' no es válida en 'sort'.";
        }

        public static final String DATO_INVALIDO_SORT(String texto1, String texto2) {
            return "La dirección de ordenamiento '" + texto1 + "' para el campo '" + texto2 + "' no es válida.";
        }

        public static final String CAMPO_INVALIDO_FILTERS(String texto) {
            return "La clave '" + texto + "' no es válida en 'filters'.";
        }

        public static final String ENTITY_PAGINATION_NUMERO_PAGINA_REQUERIDO = "El número de página es requerido.";
        public static final String ENTITY_PAGINATION_NUMERO_PAGINA_MINIMO = "El número de página debe ser al menos 1.";
        public static final String ENTITY_PAGINATION_CANTIDAD_RESULTADOS_MINIMA = "La cantidad de resultados por página debe ser al menos 1.";
        public static final String ENTITY_PAGINATION_CAMPO_ORDENAR = "Campo para ordenar";
        public static final String ENTITY_PAGINATION_DIRECCION_ORDENAMIENTO = "Dirección de ordenamiento";
        public static final String ENTITY_PAGINATION_DIRECCION_ORDENAMIENTO_ASC_DESC = "La dirección de ordenamiento debe ser 'asc' o 'desc'.";
        public static final String ENTITY_PAGINATION_CAMPO_BUSQUEDA = "Campo para realizar la búsqueda";
        public static final String ENTITY_PAGINATION_TEXTO_BUSQUEDA = "Texto de búsqueda";
        public static final String ENTITY_PAGINATION_VALORES_ORDENAR_PERMITIDOS = "El campo para ordenar debe ser uno de los valores permitidos";
        public static final String ENTITY_PAGINATION_VALORES_BUSCAR_PERMITIDOS = "El campo para buscar debe ser uno de los valores permitidos";
        public static final String ENTITY_ATRIBUTO_ELIMINAR = "Indica si el registro puede o no eliminarse: 'true'=Si se puede eliminar; 'false'=No se puede eliminar";
        public static final String ENTITY_FILECONSTRAINT_FILE_PDF_IMAGEN = "El archivo debe ser una imagen (jpg, jpeg, png) o un PDF y no superar los 5 MB.";
        public static final String UTILITIES_CATALOGS_FORENT_KEY_ID = "Identificador de la llave foranea para los catalgos que se solicitan";
        public static final String UTILITIES_CATALOGS_CATALOGOS = "Lista de nombre de los catalogos que se necesitan";
        public static final String UTILITIES_CATALOGS_CATALOGOS_VALORES_PERMITIDOS = "El catálogo debe ser uno de los valores permitidos";
        public static final String ACCESO_DENEGADO_CAMPO = "No se pudo acceder al campo ";
        public static final String LIMITE_PERMITIDO_ARCHIVO = "El archivo supera el tamaño máximo permitido";
        public static final String FORMATO_PERMITIDO_ARCHIVO = "El archivo no tiene un formato permitido";

    }

    // Módulo: Login
    public static class Login {

        public static final String ENTITY_CORREO = "Usuario";
        public static final String ENTITY_CORREO_REQUERIDO = "Usuario requerido";
        public static final String ENTITY_PASSWORD = "Contraseña";
        public static final String ENTITY_PASSWORD_REQUERIDO = "Contraseña requerida";
        public static final String ENTITY_RECAPCHAT_RESPONSE = "Respuesta del Recapchat";
        public static final String ENTITY_RECAPCHAT_RESPONSE_REQUERIDO = "Respuesta del Recapchat requerida";

        public static final String INACTIVACION_CUENTA_USUARIO = "Inactivación de cuenta por intentos fallidos";

        public static String INACTIVACION_CUENTA_USUARIO(String texto) {
            return "El usuario " + texto + " ha sido bloqueado tras varios intentos de inicio de sessión";
        }
        public static final String CREDENCIALES_INVALIDAS = "Datos de acceso incorrectos";
        public static final String CREDENCIALES_INVALIDAS_COMUNIQUESE_ADMINISTRADOR = "Datos de acceso incorrectos. Comuníquese con el administrador del sistema";
        public static final String INICIO_SESION = "Inicio de sesión";
        public static final String CIERRE_SESION = "Cierre de sesión";
        public static final String USUARIO_INACTIVO = "Cuenta de usuario inactiva";
        public static final String VALIDACION_CAPTCHA_FALLO = "La validación del captcha falló";

    }

    // Módulo: ExceptionHandler
    public static class Exception {

        public static final String DATOS_ACCESOS_INCORRECTOS = "Datos de accesos incorrectos";
        public static final String CUENTA_BLOQUEADA = "Cuenta bloqueada";
        public static final String PERMISO_DENEGADO_RECURSOS = "Permiso denegado para acceder al recurso solicitado";
        public static final String FIRMA_JWT_INVALIDA = "La firma jwt es inválida";
        public static final String TOKEN_EXPIRADO = "El token ha expirado.";

        public static final String ARCHIVO_EXCEDE_PESO(String texto) {
            return "El archivo excede el peso máximo permitido: " + texto;
        }
        public static final String FALTA_INFORMACION_SOLICITUD = "Falta información en la solicitud.";
        public static final String INFORMACION_INCORRECTA_SOLICITUD = "Información incorrecta en la solicitud.";
        public static final String URL_NO_EXISTE = "La url solicitada no existe.";
        public static final String ERROR_INTERNO_SERVIDOR = "Error interno del servidor";
        public static final String ERROR_JSON_INVALIDO = "El formato del JSON no es válido. Verifica que los valores sean correctos.";
        public static final String CONSTRAINT_VIOLATION = "Violación de restricciones.";
        public static final String ERROR_PARAMETRO = "Error en el parametro: ";
        public static final String ERROR_LIMITE_MAXIMO_REQUEST_UPLOAD = "Se supero el tamaño máximo permitido por el servidor para una solicitud.";
    }

     // Módulo: Usuario
     public static class User {

        public static final String ENTITY_ID = "Identificador único del usuario";
        public static final String ENTITY_NOMBRE = "Nombre de la persona";
        public static final String ENTITY_APELLIDO_PATERNO = "Apellido paterno de la persona";
        public static final String ENTITY_APELLIDO_MATERNO = "Apellido materno de la persona";
        public static final String ENTITY_CORREO_PERSONAL = "Correo personal de la persona";
        public static final String ENTITY_TELEFONO = "Teléfono de oficina de la persona";
        public static final String ENTITY_EXTENSION = "Extensión de la oficina de la persona";
        public static final String ENTITY_CELULAR = "Celular de la persona";
        public static final String ENTITY_CARGO = "Cargo de la persona";
        public static final String ENTITY_ROL = "Rol de la persona";
        public static final String ENTITY_ACTIVE = "Estatus del usuario";
        public static final String CONTRASEÑA_VACIA = "La contraseña no puede estar vacía";
        public static final String ENTITY_USUARIO_ID_REQUERIDO = "El id del usuario es requerido.";
        public static final String ENTITY_USUARIO_NOMBRE_REQUERIDO = "El nombre es requerido.";
        public static final String ENTITY_USUARIO_APELLIDO_PATERNO_REQUERIDO = "El apellido paterno es requerido.";
        public static final String ENTITY_USUARIO_APELLIDO_MATERNO_REQUERIDO = "El apellido paterno es requerido.";
        public static final String ENTITY_USUARIO_TELEFONO_REQUERIDO = "El correo institucional es requerido.";
        public static final String ENTITY_USUARIO_EMAIL_PERSONAL_REQUERIDO = "El correo personal es requerido.";
        public static final String ENTITY_USUARIO_EMAIL_PERSONAL_INVALIDO = "El correo personal no es válido.";
        public static final String ENTITY_USUARIO_CARGO_REQUERIDO = "El cargo es requerido.";
        public static final String ENTITY_USUARIO_EXTENSION_REQUERIDO = "La extensión es requerido.";
        public static final String ENTITY_USUARIO_TELEFONO_TAMANIO_TEXTO = "La longitud del teléfono no debe ser menor que 7 y mayor que 10 caracteres.";
        public static final String ENTITY_USUARIO_EXTENSION_TAMANIO_TEXTO = "La extensión no debe ser menor que 4 y mayor que 6 caracteres.";
        public static final String ENTITY_USUARIO_CELULAR_REQUERIDO = "El celular es requerido.";
        public static final String ENTITY_USUARIO_ROL_REQUERIDO = "El rol es requerido.";
        public static final String ENTITY_USUARIO_ACTIVO_REQUERIDO = "El estatus del usuario es requerido.";

        public static final String ENTITY_USUARIO_NOMBRE_TAMANIO_TEXTO = "El nombre no debe ser menor que 5 y mayor que 40 caracteres.";
        public static final String ENTITY_USUARIO_APELLIDO_TAMANIO_TEXTO = "El apellido no debe ser menor que 5 y mayor que 40 caracteres.";
        public static final String ENTITY_USUARIO_CARGO_TAMANIO_TEXTO = "El cargo no debe ser menor que 5 y mayor que 50 caracteres.";
        public static final String ENTITY_USUARIO_CELULAR_TAMANIO_TEXTO = "La longitud del celular no debe ser menor que 7 y mayor que 10 caracteres.";

        public static final String INFORMACION_INCORRECTA_ORDENAMIENTO = "Información incorrecta para el campo de ordenamiento";
        public static final String INFORMACION_INCORRECTA_BUSQUEDA = "Información incorrecta para el campo de búsqueda";
        public static final String EXISTE_USUARIO_SISTEMA = "El usuario ya se encuentra registrado en el sistema";
        public static final String NO_REACTIVACION_EXISTE_CORREO_SISTEMA = "No se puede reactivar, porque su correo esta registrado con otro usuario";
        public static final String CREACION_USUARIO = "Alta de usuario";
        public static final String ACTUALIZACION_USUARIO = "Modificación de usuario";
        public static final String CREDENCIALES_ENVIADAS = "Las credenciales de acceso fueron enviadas al correo institucional";
        public static final String CREDENCIALES_NO_ENVIADAS = "Sin embargo las credenciales de acceso no pudieron ser envíadas al correo institucional";
        public static final String BAJA_USUARIO = "Baja de usuario";

        public static final String USUARIO_ACTIVADO(String texto) {
            return "El usuario " + texto + " ha sido activado";
        }

        public static final String USUARIO_DESACTIVADO(String texto) {
            return "El usuario " + texto + " ha sido desactivado";
        }

        public static final String RESETEO_CONTRASENIA_USUARIO(String texto) {
            return "Reseteo de contraseña del usuario con id " + texto;
        }

        public static final String RESETEO_PASSWORD_USUARIO = "Reseteo de contraseña de usuario";
        public static final String MSJ_CORREO_RESETEO_CONTRASENIA = "Se realizó el reseteo de su contraseña para el uso del sistema de atención ciudadana";
        public static final String MSJ_CORREO_CREACION_ACCESOS = "Se ha creado usuario de acceso al Sistema de Atención Ciudadana:";
        public static final String MSJ_CORREO_CONTRASENIA_NUEVA = "Contraseña nueva";
        public static final String MSJ_CORREO_CONTRASENIA = "Contraseña";
        public static final String OPCIONES_VALIDAS_PAGINACION = "usuario,nombre,apellidoPaterno,apellidoMaterno,correoPersonal,cargo,telefono,celular,rol.rol,activo";
    }

    // Módulo: Civil
    public static class Civil{
        public static final String ENTITY_FUNCIONARIO_ID_REQUERIDO = "El id del funcionario es requerido.";
        public static final String ENTITY_FUNCIONARIO_NOMBRE_REQUERIDO = "El nombre es requerido.";
        public static final String ENTITY_FUNCIONARIO_NOMBRE_TAMANIO_TEXTO = "El nombre no debe ser menor que 2 y mayor que 30 caracteres.";
        public static final String ENTITY_FUNCIONARIO_APELLIDO_TAMANIO_TEXTO = "El apellido no debe ser menor que 2 y mayor 30 40 caracteres.";
        public static final String ENTITY_FUNCIONARIO_APELLIDO_PATERNO_REQUERIDO = "El apellido paterno es requerido.";
        public static final String ENTITY_FUNCIONARIO_APELLIDO_MATERNO_REQUERIDO = "El apellido materno es requerido.";
        public static final String ENTITY_ID = "Identificador único del funcionario";
        public static final String ENTITY_NOMBRE = "Nombre de la persona";
        public static final String ENTITY_APELLIDO_PATERNO = "Apellido paterno de la persona";
        public static final String ENTITY_APELLIDO_MATERNO = "Apellido materno de la persona";
        public static final String ENTITY_TELEFONO = "Teléfono de la persona";
        public static final String ENTITY_FUNCIONARIO_TELEFONO_REQUERIDO = "El teléfono es requerido.";
        public static final String ENTITY_FUNCIONARIO_TELEFONO_TAMANIO_TEXTO = "La longitud del teléfono no debe ser menor que 7 y mayor que 10 caracteres.";

    }

    // Módulo: TruckType
    public static class TruckType{
        public static final String ENTITY_TIPO_CAMIONETA_ID_REQUERIDO = "El id del tipo de camioneta es requerido.";
        public static final String ENTITY_TIPO_CAMIONETA_NOMBRE_REQUERIDO = "El nombre es requerido.";
        public static final String ENTITY_TIPO_CAMIONETA_NOMBRE_TAMANIO_TEXTO = "El nombre no debe ser menor que 2 y mayor que 30 caracteres.";
        public static final String ENTITY_TIPO_CAMIONETA_MODELO_TAMANIO_TEXTO = "El modelo no debe ser menor que 2 y mayor que 30 caracteres.";
        public static final String ENTITY_ID = "Identificador único del tipo de camioneta";
        public static final String ENTITY_NOMBRE = "Nombre del tipo de camioneta";
        public static final String ENTITY_MODELO = "Modelo del tipo de camioneta";
        public static final String ENTITY_TIPO_CAMIONETA_MODELO_REQUERIDO = "El nombre es requerido.";
        public static final String ENTITY_COMBUSTIBLE_ID_REQUERIDO = "El combustible es requerido.";
        public static final String ENTITY_COMBUSTIBLE_ID = "Combustible de la camioneta";    
    
    }

    // Módulo: HistoryCard
    public static class HistoryCard{
        public static final String ENTITY_ID = "Identificador único del historial de la tarjeta";
        public static final String ENTITY_ID_REQUERIDO = "Identificador único del historial de la tarjeta es requerido.";
        public static final String ENTITY_ANTES = "Valor de la tarjeta antes de realizar la accion";
        public static final String ENTITY_ANTES_REQUERIDO = "Valor de la tarjeta antes de realizar la accion es requerido.";
        public static final String ENTITY_DESPUES = "Valor de la tarjeta despues de realizar la accion";
        public static final String ENTITY_DESPUES_REQUERIDO = "Valor de la tarjeta despues de realizar la accion es requerido.";
    }

    public static class Car{
        public static final String ENTITY_ID = "Identificador único del vehiculo";
        public static final String ENTITY_VEHICULO_ID_REQUERIDO = "El id del vehiculo es requerido.";
        public static final String ENTITY_VEHICULO_NOMBRE_REQUERIDO = "El nombre es requerido.";
        public static final String ENTITY_VEHICULO_NOMBRE_TAMANIO_TEXTO = "El nombre no debe ser menor que 2 y mayor que 80 caracteres.";
        public static final String ENTITY_VEHICULO_NUMERO_SERIE_REQUERIDO = "El numero de serie es requerido.";
        public static final String ENTITY_VEHICULO_NUMERO_SERIE_TAMANIO_TEXTO = "El numero de serie no debe ser menor que 2 y mayor que 30 caracteres.";
        public static final String ENTITY_VEHICULO_COLOR_REQUERIDO = "El color es requerido.";
        public static final String ENTITY_VEHICULO_COLOR_TAMANIO_TEXTO = "El color no debe ser menor que 2 y mayor que 20 caracteres.";
        public static final String ENTITY_VEHICULO_MARCA_REQUERIDO = "La marca es requerido.";
        public static final String ENTITY_VEHICULO_MARCA_TAMANIO_TEXTO = "La marca no debe ser menor que 2 y mayor que 20 caracteres.";
        public static final String ENTITY_VEHICULO_PLACAS_REQUERIDO = "La placa es requerido.";
        public static final String ENTITY_VEHICULO_PLACAS_TAMANIO_TEXTO = "La placa no debe ser menor que 2 y mayor que 20 caracteres.";
        public static final String ENTITY_VEHICULO_CDFI_REQUERIDO = "El CDFI es requerido.";
        public static final String ENTITY_VEHICULO_CDFI_TAMANIO_TEXTO = "El CFDI no debe ser menor que 2 y mayor que 20 caracteres.";
        public static final String ENTITY_NOMBRE = "Nombre del vehiculo";
        public static final String ENTITY_VEHICULO_NUMERO_SERIE = "Numero de serie del vehiculo";
        public static final String ENTITY_VEHICULO_COLOR = "Color del vehiculo";
        public static final String ENTITY_VEHICULO_MARCA = "Marca del vehiculo";
        public static final String ENTITY_VEHICULO_PLACAS = "Placas del vehiculo";
        public static final String ENTITY_VEHICULO_CDFI = "CDFI del vehiculo";
        public static final String ENTITY_VEHICULO_OBSERVACIONES = "Observaciones del vehiculo";
        public static final String ENTITY_TIPO_CAMIONETA_ID_REQUERIDO = "El tipo de camioenta es requerido.";
        public static final String ENTITY_TIPO_CAMIONETA_ID = "El tipo de vehiculo";    
        public static final String ENTITY_AREA_ASIGNACION_ID_REQUERIDO = "El tipo de camioenta es requerido.";
        public static final String ENTITY_AREA_ASIGNACION_ID = "El tipo de vehiculo";    
    }

    //Modulo cargar tarjeta
    public static class LoadCard {
        public static final String ENTITY_ID = "Identificador único del cargado de tarjeta";
        public static final String ENTITY_ID_REQUERIDO = "Identificador único del cargado de tarjeta es requerido.";
        public static final String ENTITY_USUARIO_ID_REQUERIDO = "El id del usuario es requerido.";
        public static final String ENTITY_USUARIO_ID = "El id del usuario.";
        public static final String ENTITY_OBSERVACIONES = "Observaciones de la carga";
        public static final String ENTITY_CANTIDAD = "Cantidad a cargar a la tarjeta";
        public static final String ENTITY_CANTIDAD_REQUERIDO = "Cantidad a cargar a la tarjeta es requerido.";
    }

    //Moduclo Bitacora de combustible
    public static class BinnacleFuel{
        public static final String ENTITY_ID = "Identificador único del cargado de combustible";
        public static final String ENTITY_ID_REQUERIDO = "Identificador único del cargado de combustible es requerido.";
        public static final String ENTITY_FUNCIONARIO_ID = "El funcionario que opera el vehiculo";    
        public static final String ENTITY_FUNCIONARIO_ID_REQUERIDO = "El funcionario que opera el vehiculo es requerido.";
        public static final String ENTITY_COMBUSTIBLE_ID = "El tipo de combustible";
        public static final String ENTITY_COMBUSTIBLE_ID_REQUERIDO = "El tipo de combustible es requerido.";
        public static final String ENTITY_USUARIO_ID = "El usuario que realiza la carga";
        public static final String ENTITY_USUARIO_ID_REQUERIDO = "El usuario que realiza la carga es requerido.";
        public static final String ENTITY_VEHICULO_ID = "El vehiculo que se le carga combustible";
        public static final String ENTITY_VEHICULO_ID_REQUERIDO = "El vehiculo que se le carga combustible es requerido.";
        public static final String ENTITY_CANTIDAD = "Cantidad de combustible";
        public static final String ENTITY_CANTIDAD_REQUERIDO = "Cantidad de combustible es requerido.";
        public static final String ENTITY_FECHA = "Fecha de la carga de combustible";
        public static final String ENTITY_FECHA_REQUERIDO = "Fecha de la carga de combustible es requerido.";
        public static final String ENTITY_OBSERVACIONES = "Observaciones de la carga de combustible";
        public static final String ENTITY_KILOMETRAJE_INICIAL = "Kilometraje inicial del vehiculo";
        public static final String ENTITY_KILOMETRAJE_INICIAL_REQUERIDO = "Kilometraje inicial del vehiculo es requerido.";
        public static final String ENTITY_KILOMETRAJE_FINAL = "Kilometraje final del vehiculo";
        public static final String ENTITY_KILOMETRAJE_FINAL_REQUERIDO = "Kilometraje final del vehiculo es requerido.";
        public static final String ENTITY_GASTO = "Gasto del vehiculo";
        public static final String ENTITY_GASTO_REQUERIDO = "Gasto del vehiculo es requerido.";
        public static final String ENTITY_LITROS = "Listros gastados del vehiculo";
        public static final String ENTITY_MOTIVO_RECORRIDO = "Motivo del recorrido";
        public static final String ENTITY_LUGAR = "Lugar de la carga de combustible";
        public static final String ENTITY_LUGAR_REQUERIDO = "Lugar de la carga de combustible es requerido.";
        public static final String ENTITY_LISTA_DOCUMENTOS = "Lista de documentos de la carga de combustible";
    }

}
