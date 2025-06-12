import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Base64;
import java.io.*;
import java.util.ResourceBundle;
import java.net.URL;

class Constante { //Conectarse a la base de datos
    public static final String USERNAME = "is150401";
    public static final String PASSWORD = "qXczL$ZWNA2ODQ6";
    public static final String THINCONN = "jdbc:oracle:thin:@orion.javeriana.edu.co:1521/LAB";
}

public class PROYECTO3 {
    public static void main(String[] args) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String usuario;
        String contrasenia;

        try {
            Connection conex = DriverManager.getConnection(Constante.THINCONN, Constante.USERNAME, Constante.PASSWORD);
            System.out.println("--- WELCOME TO SPLITPAY ---");
            System.out.print("Usuario: ");
            usuario = bf.readLine();
            System.out.print("Contraseña: ");
            contrasenia = bf.readLine();
            System.out.println("===================================================");

            // Autenticación del usuario
            if (!autenticar(conex, usuario, contrasenia)) {
                System.out.println("Usuario no existente, por favor, cree una cuenta");
                return;
            }

            int userId = getIdUsuario(conex, usuario);
            if (userId == 0) {
                System.out.println("Error obteniendo el ID del usuario.");
                return;
            }

            // Menú Principal
            while (true) {
                System.out.println("---- SPLITPAY MAIN MENU ----");
                System.out.println("1. Ver Grupos y Deudas");
                System.out.println("2. Crear Factura (Bill)");
                System.out.println("3. Crear Grupo");
                System.out.println("4. Realizar una transaccion");
                if (verificarLider(conex, userId)){
                    System.out.println("5. Administrar un grupo");
                    System.out.println("6. Salir");
                }
                else{
                    System.out.println("5. Salir");
                }
                System.out.print("Selecciona una opción: ");
                int opcion = Integer.parseInt(bf.readLine());

                if (verificarLider(conex, userId)){
                    switch (opcion) {
                        case 1 -> mostrarDeudasGrupo(conex, userId);
                        case 2 -> crearFacturaMenu(conex, userId);
                        case 3 -> crearGrupoMenu(conex);
                        case 4 -> transaccion(conex, userId);
                        case 5 -> adminGrupo(conex, userId);
                        case 6 -> {
                            System.out.println("Cerrando sesión...");
                            return;
                        }
                        default -> System.out.println("Opción no válida. Inténtalo de nuevo.");
                    }
                }
                else{
                    switch (opcion) {
                        case 1 -> mostrarDeudasGrupo(conex, userId);
                        case 2 -> crearFacturaMenu(conex, userId);
                        case 3 -> crearGrupoMenu(conex);
                        case 4 -> transaccion(conex, userId);
                        case 5 -> {
                            System.out.println("Cerrando sesión...");
                            return;
                        }
                        default -> System.out.println("Opción no válida. Inténtalo de nuevo.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static boolean autenticar(Connection conex, String usuario, String contrasenia) {
        String SQL = "SELECT EMAIL, CONTRASENIA FROM Usuario";
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();{
                while (rs.next()){
                    //System.out.println("Correo: "+rs.getString("EMAIL"));
                    if ((usuario.equals(rs.getString("EMAIL")))&&(contrasenia.equals(rs.getString("CONTRASENIA")))){
                        return true;
                    }
                }
            }
        }catch (SQLException ex){
            System.out.println("Error de conexion:" + ex.toString());
            ex.printStackTrace();
        }
        return false;
    }
    public static void mostrarDeudasGrupo(Connection conex, int userId) {
        String SQL = """
                SELECT 
                    g.id AS group_id,
                    g.nombre AS group_name,
                    NVL(SUM(CASE WHEN d.estado = 'P' THEN d.monto ELSE 0 END), 0) AS total_debt_in_group
                FROM miembrogrupo mg
                JOIN grupo g ON mg.grupo_id = g.id
                LEFT JOIN deuda d ON (
                    d.miembrogrupo_usuario_id = mg.usuario_id 
                    AND d.miembrogrupo_grupo_id = mg.grupo_id
                )
                WHERE mg.usuario_id = ?
                AND mg.estado = 'A'
                AND g.estado = 'A'
                GROUP BY g.id, g.nombre
                ORDER BY total_debt_in_group DESC
                """;
        
        try {
            PreparedStatement ps = conex.prepareStatement(SQL);
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
             
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Group ID: " + rs.getInt("group_id"));
                System.out.println("Group Name: " + rs.getString("group_name"));
                System.out.println("Total Debt: $" + rs.getDouble("total_debt_in_group"));
                System.out.println("Selecciona este grupo con ID: " + rs.getInt("group_id") + " para ver más detalles");
                System.out.println("--------------------");
            }
            System.out.print("Ingresa el ID del grupo que deseas ver a detalle, o 0 para regresar: ");
            int selectedGroupId = Integer.parseInt(bf.readLine());
            if (selectedGroupId != 0) {
                verGrupo(conex, selectedGroupId);
            }
        } catch (Exception ex) {
            System.out.println("Error de conexión: " + ex.toString());
            ex.printStackTrace();
        }
    }

    public static void verGrupo(Connection conex, int groupId) {
        String SQL = "SELECT * FROM GRUPO WHERE id = ?";
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);

            System.out.println(" --- Mostrando detalles del grupo con ID: " + groupId+" ---");
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Group ID: " + rs.getInt("id"));
                System.out.println("Group Name: " + rs.getString("nombre"));
                System.out.println("Group status: " + rs.getString("estado"));
                System.out.println("Fecha de creacion "+ rs.getDate("fecha_creacion"));
            }

        }catch(Exception e){
            System.out.println("Error en ver Grupo "+e.getMessage());
        }
    }

    public static void crearFacturaMenu(Connection conex, int userId) throws Exception {
        String SQL = "INSERT INTO INSERT INTO factura VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conex.prepareStatement(SQL);
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ImageView imagen;

        try{
            System.out.println("---- CREAR FACTURA ----");
            System.out.print("Ingrese el codigo de la factura: ");
            int codigo = Integer.parseInt(bf.readLine());
            System.out.print("Ingrese la descripción de la factura: ");
            String descripcion = bf.readLine();
            System.out.print("Ingrese el método de pago: ");
            String metodoPago = bf.readLine();
            System.out.print("Ingrese el estado de la factura (P/A): ");
            char estado = bf.readLine().charAt(0);
            System.out.print("Ingrese el monto de la factura: ");
            double monto = Double.parseDouble(bf.readLine());
            System.out.print("Ingrese la fecha de la factura: ");
            java.util.Date fecha = sdf.parse(bf.readLine());
            Date fechaBD = new Date(fecha.getTime());
            System.out.print("Ingrese la direccion del directorio donde se encuentra la foto de la factura: ");
            String ruta = bf.readLine();
            System.out.println("Ingrese la ubicacion del local donde se genero la factura: ");
            String ubi = bf.readLine();
            System.out.println("Factura Creada con Exito");
            //GUARDAR IMAGEN
            URL resourceUrl = PROYECTO3.class.getClass().getResource(ruta);
            Image imagen2 = new Image(resourceUrl.toExternalForm());
            //imagen.setImage(imagen2);
            StringReader imagenBase64 = new StringReader(convertirImagenABase64(imagen2));

            ps.setInt(1, codigo);
            ps.setString(2, descripcion);
            ps.setString(3, metodoPago);
            ps.setString(4, String.valueOf(estado));
            ps.setDouble(5, monto);
            ps.setDate(6, fechaBD);
            ps.setClob(7, imagenBase64);
            ps.setString(8, ubi);
            /*try{
            String imagen = convertirImagenABase64(ruta);//
            String sql = "INSERT INTO Factura VALUES (?, ?, ?, ?, ?,)?, ?, ?";
            PreparedStatement statement = conex.prepareStatement(sql);
            statement.setInt(1, codigo);
            statement.setString(2, descripcion);
            statement.setString(3, metodoPago);
            statement.setString(4, String.valueOf(estado));
            statement.setDouble(5, monto);
            statement.setDate(6, fechaBD);
            statement.setString(7, imagen);
            statement.setString(8, ubi);
            
            int filasModificadas = statement.executeUpdate();
            if (filasModificadas > 7) {
                System.out.println("Imagen guardada correctamente en la base de datos");
            }
            
            statement.close();
            } catch (Exception e) {
            e.printStackTrace();
            }*/
        } catch(Exception e){
            System.out.println("Error en crear factura: "+e.getMessage());
        }
    }

    public static void crearGrupoMenu(Connection conex) throws Exception {
        String SQL = "INSERT INTO grupo VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conex.prepareStatement(SQL);
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("---- CREAR GRUPO ----");
        System.out.print("Ingrese el id del nuevo grupo: ");
        int id = Integer.parseInt(bf.readLine());
        System.out.print("Ingrese el nombre del grupo: ");
        String nombreGrupo = bf.readLine();
        System.out.print("Ingrese estado del grupo (A/D): ");
        String estado = bf.readLine();
        System.out.print("Ingrese la fecha de creacion del grupo: ");
        java.util.Date fecha = sdf.parse(bf.readLine());
        Date fechaDB = new Date(fecha.getTime());

        ps.setInt(1, id);
        ps.setString(2, nombreGrupo);
        ps.setString(3, String.valueOf(estado));
        ps.setDate(4, fechaDB);

        ps.executeUpdate();

        System.out.println("Grupo " + nombreGrupo + " creado con exito");
    }

    public static int getIdUsuario(Connection conex, String usuario) {
        String SQL = "SELECT ID FROM Usuario WHERE EMAIL = ?";
        try (
             PreparedStatement ps = conex.prepareStatement(SQL)) {
             
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.toString());
            ex.printStackTrace();
        }
        return 0;
    }
    public static String convertirImagenABase64(Image imagen) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(imagen, null), "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
        /*File archivoImagen = new File(rutaImagen);
        try (FileInputStream inputStream = new FileInputStream(archivoImagen)) {
            byte[] imagenBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imagenBytes);
        }*/
    }
    public static void mostrarYguardar_imagen(Connection connection, int idImagen, String rutArchivo){ //Metodo para que busque y meustre la imagen de la base de datos
        //En el parametro "idImagen" toca poner el identificador de la imagen, osea como donde está
        JFrame frame =new JFrame("IMAGEN BASE DE DATOS"); //Crea la ventana donde va a mostrar la imagen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);

        try{
            String consultasql = "SELECT imagen FROM Factura WHERE id = ?";
            PreparedStatement statement= connection.prepareStatement(consultasql);
            statement.setInt(1, idImagen);

            ResultSet resultset = statement.executeQuery();
            if(resultset.next()){
                String base64Imagen = resultset.getString("imagen");  
                
                //Decodifa a bytes
                byte[] imagenEnBytes = Base64.getDecoder().decode(base64Imagen);
                ImageIcon icono = new ImageIcon(imagenEnBytes);
                
                JLabel label = new JLabel(icono);
                frame.add(label);

                /*try (OutputStream outputStream = new FileOutputStream(rutArchivo)) {
                    outputStream.write(imagenEnBytes);
                    System.out.println("Imagen guardada en: "+ rutArchivo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
               
            }
        
        resultset.close();
        statement.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        frame.setVisible(true);
    }

    public static boolean verificarLider(Connection conex, int userId){
        String SQL = """
        SELECT DISTINCT mg.usuario_id
        FROM miembrogrupo mg 
        JOIN usuario u ON mg.usuario_id = u.id
        JOIN grupo g ON mg.grupo_id = g.id
        WHERE mg.rol = 'Líder' and u.id = ?    
                """;
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
    
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        }catch(Exception e){
            System.out.println("Error al verificar lider: "+e.getMessage());
        }
        return false;
    }

    public static void adminGrupo(Connection conex, int userId){
        String SQL = """
            select g.id as group_id, g.nombre as group_nombre
            from grupo g
            join miembrogrupo mg on mg.grupo_id = g.id
            where mg.usuario_id = ?;
                """;
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
             
            ps.setInt(1, userId);
            
            System.out.println("--- Bienvenido a la administración de grupos ---");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.print(rs.getString("group_id")+" ");
                System.out.println(rs.getString("group_name"));
            }
            System.out.print("--- Inserte el id del grupo que desea administrar: ");
            int id = Integer.parseInt(bf.readLine());
            autenticarGrupo(conex, userId, id);
            menuAdminG(conex, userId, id);
        }catch (Exception e){
            System.out.println("Error en la administracion de grupos "+e.getMessage());
        }
    }
    public static void menuAdminG(Connection conex, int userId, int groupId){
        if (autenticarGrupo(conex, userId, groupId)){
            try{
                String SQL = "SELECT * FROM GRUPO WHERE id = ?";
                PreparedStatement ps = conex.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery();
                InputStreamReader rd =  new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(rd);
                
                
                ps.setInt(1, groupId);

                System.out.println("Bienvenido a la administracion del grupo: "+rs.getString("nombre"));
                System.out.println("1. Agregar Miembro");
                System.out.println("2. Asignar un nuevo lider");
                System.out.println("3. Disolver todas las deudas del grupo");
                System.out.print("Digite la opcion que desea realizar: ");
                String opcion = br.readLine();

                switch (opcion){
                    case "1" -> agregarMiembro(conex, groupId);
                    case "2" -> asignarLider(conex, groupId);
                    case "3" -> disolverDeudas(conex, groupId);
                    default -> System.out.println("Marcacion incorrecta, intente de nuevo");
                }

            }catch(Exception e){
                System.out.println("ERROR EN LA ADMINISTRACION DE GRUPOS "+e.getMessage());
            }
        }
        else{
            System.out.println("Hemos tenido problemas para validar el grupo digitado");
            return;
        }
    }
    public static boolean autenticarGrupo(Connection conex, int userId, int groupId) {
        String SQL = """
                select g.id, u.id
                from grupo g
                join miembrogrupo mg on mg.grupo_id = g.id
                join usuario u on u.id = mg.usuario_id;
                """;
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();{
                while (rs.next())
                    if ((groupId == rs.getInt("g.id"))&&(userId == rs.getInt("u.id"))){
                        return true;
                    }
                }
        }catch (SQLException ex){
            System.out.println("Error de conexion:" + ex.toString());
            ex.printStackTrace();
        }
        return false;
    }
    public static void agregarMiembro(Connection conex, int groupId){
        String SQL = "INSERT INTO miembrogrupo VALUES (?, ?, ?, ?)";

        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            InputStreamReader rd = new InputStreamReader(System.in);
            BufferedReader bf = new BufferedReader(rd);

            System.out.println("--- Por favor, digite los siguientes datos ---");
            System.out.print("Rol dentro del grupo: ");
            String rol = bf.readLine();
            System.out.print("Estado dentro del gruoo: ");
            String estado = bf.readLine();
            System.out.print("Id del usuario a agregar: ");
            int id = Integer.parseInt(bf.readLine());
            if (!autenticarId(conex, id)){
                System.out.println("Error, el usuario no existe");
                return;
            }
            ps.setString(1, rol);
            ps.setString(2, estado);
            ps.setInt(3, id);
            ps.setInt(4, groupId);

            ps.executeUpdate();

            System.out.println("Usuario agregado con exito");
        }catch(Exception e){
            System.out.println("ERROR EN AGREGAR MIEMBRO A UN GRUPO "+e.getMessage());
        }
    }
    public static boolean autenticarId(Connection conex, int id){
        String SQL = "SELECT id from usuario";
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                if (id == rs.getInt("id")){
                    return true;
                }
            }
        }catch(Exception e){
            System.out.println("ERROR EN AUTENTICAR ID "+e.getMessage());
        }
        return false;
    }
    public static void asignarLider(Connection conex, int groupId){
        String SQL = "UPDATE miembrogrupo SET rol = ? WHERE usurio_id= ? and grupo_id = ?";
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            InputStreamReader rd = new InputStreamReader(System.in);
            BufferedReader bf = new BufferedReader(rd);
            
            System.out.print("Digite el id del usuario que desea asignar como lider: ");
            int id = Integer.parseInt(bf.readLine());
            if (!autenticarId(conex, id)){
                System.out.println("Usuario inexistente ");
                return;
            }
            ps.setString(1, "Líder");
            ps.setInt(2, id);
            ps.setInt(3, groupId);

            ps.executeUpdate();

        }catch(Exception e){
            System.out.println("ERROR AL ASIGNAR LIDER "+e.getMessage());
        }
    }
    public static void transaccion(Connection conex, int userId){
        String SQL = "UPDATE usuario SET balance = balance - ? WHERE id = ?";
        String SQL2 = "UPDATE usuario SET balance = balance + ? WHERE id = ?";
        String SQL3 = "INSERT INTO transaccion VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement ps = conex.prepareStatement(SQL);
            PreparedStatement ps2 = conex.prepareStatement(SQL2);
            PreparedStatement ps3 = conex.prepareStatement(SQL3);

            InputStreamReader rd = new InputStreamReader(System.in);
            BufferedReader bf = new BufferedReader(rd);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            System.out.print("Digite el numero (codigo) de transaccion: ");
            int codigo = Integer.parseInt(bf.readLine());
            System.out.print("Digite el id del usuario a quien desea realizar la transaccion: ");
            int id = Integer.parseInt(bf.readLine());
            System.out.print("Digite el monto: ");
            Double monto = Double.parseDouble(bf.readLine());
            System.out.print("Digite la fecha de la transaccion: ");
            java.util.Date fecha = sdf.parse(bf.readLine());
            Date fechaBD = new Date(fecha.getTime());
            System.out.print("Digite el codigo de la deuda referente a la transaccion: ");
            int codigoDeuda = Integer.parseInt(bf.readLine());
            if (!verificarCodigo(conex, "deuda", "codigo_deuda", codigoDeuda)){
                System.out.println("Deuda inexistente");
                return;
            }
            System.out.print("Digite el codigo de la factura referente a la transaccion: ");
            int codigoFactura = Integer.parseInt(bf.readLine());
            if (!verificarCodigo(conex, "factura", "codigo", codigoFactura)){
                System.out.println("Factura inexistente");
            }
            System.out.print("Digite el codigo del grupo del usuario al que se le trasferira el dinero: ");
            int codigoGrupo = Integer.parseInt(bf.readLine());
            if (!autenticarGrupo(conex, id, codigoGrupo)){
                System.out.println("Usuario no registrado en el grupo o grupo inexistente");
            }
            System.out.println("Digite el codigo del grupo del usuario que hace la transaccion: ");
            int codigoGrupo2 = Integer.parseInt(bf.readLine());
            if (!autenticarGrupo(conex, userId, codigoGrupo2)){
                System.out.println("Usuario no registrado en el grupo o grupo inexistente");
            }
            ps.setDouble(1, monto);
            ps.setInt(2, userId);

            ps2.setDouble(1, monto);
            ps2.setInt(2, id);

            ps3.setInt(1, codigo);
            ps3.setDouble(2, monto);
            ps3.setDate(3, fechaBD);
            ps3.setInt(4, codigoDeuda);
            ps3.setInt(5, codigoFactura);
            ps3.setInt(6, id);
            ps3.setInt(7, codigoGrupo);
            ps3.setInt(8, userId);
            ps3.setInt(9, codigoGrupo2);

            ps.executeUpdate();
            ps2.executeUpdate();
            ps3.executeUpdate();
        }catch(Exception e){
            System.out.println("ERROR EN TRANSACCION "+e.getMessage());
        }
    }
    public static void Mostrar_Factura(Connection conex){
        String sql = """
                SELECT TO_CHAR(fecha, 'YYYY-MON') AS añomes , ubicacion, SUM(monto) AS total 
                FROM Factura WHERE estado = 'A' GROUP BY TO_CHAR(fecha, 'YYYY-MON'), ubicacion
                """;

        try(PreparedStatement st = conex.prepareStatement(sql); ResultSet rs = st.executeQuery(sql)){

            ArrayList<String> fechas = new ArrayList<>();
            ArrayList<String> grupos = new ArrayList<>();
            ArrayList<ArrayList<Double>> tablaDatos = new ArrayList<>();
            while(rs.next()){
                String fecha = rs.getString("añomes");
                String grupo = rs.getString("ubicacion");
                double totalDouble = rs.getDouble("total");

                if(!fecha.contains(fecha)) {
                    fechas.add(fecha);
                    tablaDatos.add(new ArrayList<>());
                }
                if(!grupos.contains(grupo)){
                    grupos.add(grupo);
                
                }

                int fila = fecha.indexOf(fecha);
                int columna = grupos.indexOf(grupo);
                
                while(tablaDatos.get(fila).size()<=columna){
                    tablaDatos.get(fila).add(0.0);
                }
                tablaDatos.get(fila).set(columna, totalDouble);
            }

            System.out.printf("%-15s", "AñoMes del Bill");
            for (String grupo : grupos) {
                System.out.printf("%-15s", grupo);
            }
            System.out.printf("%-15s%n", "Totales");

            for (int i = 0; i < fechas.size(); i++) {
                String fecha = fechas.get(i);
                System.out.printf("%-15s", fecha);
                double totalFila = 0;
                for (int j = 0; j < grupos.size(); j++) {
                    double valor = j < tablaDatos.get(i).size() ? tablaDatos.get(i).get(j) : 0.0;
                    System.out.printf("%-15.2f", valor);
                    totalFila += valor;
                }
                System.out.printf("%-15.2f%n", totalFila);
            }
            
            System.out.printf("%-15s", "Total");
            double totalGeneral = 0;

            for (int j = 0; j < grupos.size(); j++) {
                double totalColumna = 0;
                for (int i = 0; i < fechas.size(); i++) {
                    if (j < tablaDatos.get(i).size()) {
                        totalColumna += tablaDatos.get(i).get(j);
                    }
                }
                System.out.printf("%-15.2f", totalColumna);
                totalGeneral += totalColumna;
            }
            System.out.printf("%-15.2f%n", totalGeneral);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void disolverDeudas(Connection conex, int group_id){
        String SQL = "{call DEUDA_PKG.eliminar_deudas_grupo(?)}";

        try{
            CallableStatement call = conex.prepareCall(SQL);

            call.setInt(1, group_id);
            call.execute();
        }catch(Exception e){
            System.out.println("ERROR EN DISOLVER DEUDAS "+e.getMessage());
        }
    }
    public static boolean verificarCodigo(Connection conex, String tabla, String columna, int codigo){
        String sql= "SELECT COUNT(*) FROM "+ tabla + "WHERE" + columna + "= ?";

        try{
            PreparedStatement ps = conex.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ps.setInt(1, codigo);
            try{
                if(rs.next()){
                    return rs.getInt(1)>0;
                }
            }catch(Exception x){
                System.out.println("ERROR EN VERIFICAR CODIGO "+x.getMessage());
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}