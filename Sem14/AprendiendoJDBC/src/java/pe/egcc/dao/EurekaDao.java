package pe.egcc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import pe.egcc.db.AccesoDB;
import pe.egcc.domain.Cliente;
import pe.egcc.domain.Empleado;

public class EurekaDao {

  public void addParametro(String codigo, String descripcion, String valor) {
    Connection cn = null;
    try {
      cn = AccesoDB.getConnection();
      String sql = "insert into parametro(chr_paracodigo,"
              + "vch_paradescripcion,vch_paravalor,vch_paraestado) "
              + "values(?,?,?,?)";
      PreparedStatement pstm = cn.prepareStatement(sql);
      pstm.setString(1, codigo);
      pstm.setString(2, descripcion);
      pstm.setString(3, valor);
      pstm.setString(4, "ACTIVO");
      pstm.executeUpdate();
      pstm.close();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    } finally {
      try {
        cn.close();
      } catch (Exception e) {
      }
    }
  }

  /**
   * Permite buscar clientes por codigo, nombre, paterno y materno.
   *
   * @param cliente Se utiliza un parametro de tipo Cliente para comunicar los criterios de busqueda.
   * @return Retorna una lista de objetos de tipo Cliente.
   */
  public List<Cliente> getClientes(Cliente cliente) {
    List<Cliente> lista = new ArrayList();
    Connection cn = null;
    try {
      cn = AccesoDB.getConnection();
      String sql = "select chr_cliecodigo, vch_cliepaterno,"
              + "vch_cliematerno, vch_clienombre,"
              + "chr_cliedni, vch_clieciudad,"
              + "vch_cliedireccion, vch_clietelefono,"
              + "vch_clieemail from cliente "
              + "where chr_cliecodigo like concat(?,'%') "
              + "and vch_cliepaterno like concat(?,'%') "
              + "and vch_cliematerno like concat(?,'%') "
              + "and vch_clienombre like concat(?,'%') ";
      PreparedStatement pstm = cn.prepareStatement(sql);
      pstm.setString(1, cliente.getCodigo());
      pstm.setString(2, cliente.getPaterno());
      pstm.setString(3, cliente.getMaterno());
      pstm.setString(4, cliente.getNombre());
      ResultSet rs = pstm.executeQuery();
      while (rs.next()) {
        Cliente bean = new Cliente();
        bean.setCodigo(rs.getString("chr_cliecodigo"));
        bean.setPaterno(rs.getString("vch_cliepaterno"));
        bean.setMaterno(rs.getString("vch_cliematerno"));
        bean.setNombre(rs.getString("vch_clienombre"));
        bean.setDni(rs.getString("chr_cliedni"));
        bean.setCiudad(rs.getString("vch_clieciudad"));
        bean.setDireccion(rs.getString("vch_cliedireccion"));
        bean.setTelefono(rs.getString("vch_clietelefono"));
        bean.setEmail(rs.getString("vch_clieemail"));
        lista.add(bean);
      }
      rs.close();
      pstm.close();
    } catch (Exception e) {
      throw new RuntimeException("Error al consultar clientes.");
    } finally {
      try {
        cn.close();
      } catch (Exception e) {
      }
    }
    return lista;
  }

  public Empleado validar(String usuario, String clave) {
    Empleado bean = null;
    Connection cn = null;
    try {
      cn = AccesoDB.getConnection();
      String sql = "select chr_emplcodigo, vch_emplpaterno, "
              + "vch_emplmaterno, vch_emplnombre, "
              + "vch_emplciudad, vch_empldireccion, "
              + "vch_emplusuario from empleado "
              + "where vch_emplusuario = ?  and  vch_emplclave = ? ";
      PreparedStatement pstm = cn.prepareStatement(sql);
      pstm.setString(1, usuario);
      pstm.setString(2, clave);
      ResultSet rs = pstm.executeQuery();
      if (rs.next()) {
        bean = new Empleado();
        bean.setCodigo(rs.getString("chr_emplcodigo"));
        bean.setPaterno(rs.getString("vch_emplpaterno"));
        bean.setMaterno(rs.getString("vch_emplmaterno"));
        bean.setNombre(rs.getString("vch_emplnombre"));
        bean.setCiudad(rs.getString("vch_emplciudad"));
        bean.setDireccion(rs.getString("vch_empldireccion"));
        bean.setUsuario(rs.getString("vch_emplusuario"));
      }
      rs.close();
      pstm.close();
    } catch (Exception e) {
      throw new RuntimeException("Error al consultar empleado.");
    } finally {
      try {
        cn.close();
      } catch (Exception e) {
      }
    }
    return bean;
  }

  public void procDeposito(String cuenta, double importe, String codEmp) {
    String SQL_SELECT = "select dec_cuensaldo saldo, int_cuencontmov cont "
            + "from cuenta where chr_cuencodigo = ? for update ";
    String SQL_UPDATE = "update cuenta set dec_cuensaldo=?, "
            + "int_cuencontmov=? where chr_cuencodigo = ?";
    String SQL_INSERT = "insert into movimiento(chr_cuencodigo,int_movinumero, "
            + "dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte) "
            + "values(?,?,sysdate(),?,'003',?)";
    Connection cn = null;
    try {
      // Inicio de Tx
      cn = AccesoDB.getConnection();
      cn.setAutoCommit(false);
      // Leer los datos
      PreparedStatement pstm = cn.prepareStatement(SQL_SELECT);
      pstm.setString(1, cuenta);
      ResultSet rs = pstm.executeQuery();
      if (!rs.next()) {
        throw new RuntimeException("ERROR, cuenta no existe.");
      }
      double saldo = rs.getDouble("saldo");
      int cont = rs.getInt("cont");
      rs.close();
      pstm.close();
      // Actualizar variables
      saldo += importe;
      cont++;
      // Actualizar cuenta
      pstm = cn.prepareStatement(SQL_UPDATE);
      pstm.setDouble(1, saldo);
      pstm.setInt(2, cont);
      pstm.setString(3, cuenta);
      pstm.executeUpdate();
      pstm.close();
      // Insertar movimiento
      pstm = cn.prepareStatement(SQL_INSERT);
      pstm.setString(1, cuenta);
      pstm.setInt(2, cont);
      pstm.setString(3, codEmp);
      pstm.setDouble(4, importe);
      pstm.executeUpdate();
      pstm.close();
      // Confirmar Tx
      cn.commit();
    } catch (Exception e) {
      try {
        cn.rollback();
      } catch (Exception e1) {
      }
      String error ="Error en la transacción DEPOSITO.";
      if(e != null && !e.getMessage().isEmpty()){
        error += "\n" + e.getMessage();
      }
      throw new RuntimeException(error);
    } finally {
      try {
        cn.close();
      } catch (Exception e) {
      }
    }
  }

}
