package app.controller.login;

import app.controller.dashboard.DashboardController;
import app.error.msg;
import app.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class LoginController {
    public TextField userInserido;
    public PasswordField passInserida;
    public Button loginBtn;


    //  login Funcionario

    public void butEntrarClic(ActionEvent actionEvent) throws SQLException, IOException, ParseException {

        Connection conn = Util.criarConexao();

        String pass = this.passInserida.getText();
        String user = this.userInserido.getText();

        PreparedStatement pst = conn.prepareStatement("SELECT ID_FUNCIONARIO, TIPO_FUNCIONARIO, USERNAME, PW, ID_EMPRESA" +
                " FROM FUNCIONARIO WHERE USERNAME LIKE ? AND PW LIKE ?");

        pst.setString(1, user);
        pst.setString(2, pass);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            int cargoPerm = rs.getInt("TIPO_FUNCIONARIO");

            int idLog = rs.getInt("ID_FUNCIONARIO");

            PreparedStatement pst2 = conn.prepareStatement("SELECT e.NOME_EMPRESA,f.ID_EMPRESA," +
                    "f.ID_FUNCIONARIO, f.ESTADO FROM EMPRESA e," +
                    "FUNCIONARIO f " +
                    "WHERE e.ID_EMPRESA = f.ID_EMPRESA AND f.ID_FUNCIONARIO = ?");

            pst2.setInt(1, idLog);

            ResultSet rs1 = pst2.executeQuery();


            if (rs1.next()) {

                if (rs1.getInt("ESTADO") != 0) {


                    if (cargoPerm == 21 || cargoPerm == 22 || cargoPerm == 23 || cargoPerm == 1) {

                        System.out.println("Login com sucesso!");

                        // VER NOME DA EMPRESA
                        int empresa = rs1.getInt("ID_EMPRESA");
                        String username = rs.getString("USERNAME");
                        String nomeEmpresa = rs1.getString("NOME_EMPRESA");
                        int idLog1 = rs1.getInt("ID_FUNCIONARIO");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/ui/dashboard/dashboard.fxml"));
                        Parent root = loader.load();
                        Stage stage = new Stage();
                        stage.setTitle("EmpresaVinhos | Dashboard");
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.getIcons().add(new Image("/img/logo.png"));
                        stage.show();
                        DashboardController dashboard = loader.getController();
                        dashboard.iniciar(empresa, idLog1, username, nomeEmpresa, cargoPerm);
                        //((Node) (actionEvent.getSource())).getScene().getWindow().hide();

                        closeLogin(loginBtn);

                    } else {
                        System.out.println("Utilizador sem cargo associado para login");
                    }


                } else {
                    System.out.println("O utilizador não tem permissão para entrar na aplicação!");
                    msg.alertaAviso("O utilizador não tem permissão para entrar na aplicação!", "Aviso!", "Dados desativados!");

                }
            } else {
                System.out.println("Falha ao procurar empresa do utilizador Logado!");
                msg.alertaErro("Falha ao procurar empresa do utilizador Logado!", "Erro!", "Falha ao procurar empresa do utilizador Logado!");
            }


        } else {
            if (user.isEmpty()) {
                msg.alertaAviso("Por favor insira o username!", "Aviso!", "Username não pode estar vazio!");
            } else if (pass.isEmpty()) {
                msg.alertaAviso("Por favor insira a password!", "Aviso!", "Password não pode estar vazia!");
            } else {
                msg.alertaErro("Corrija o utilizador e/ou a password!", "Erro!", "Username e password não correspondem!");
            }
        }

    }

    public void closeLogin(Button btn) throws FileNotFoundException, ParseException
    {
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void buttonPressed(KeyEvent e) {
        if (e.getCode().toString().equals("ENTER")) {
            loginBtn.fire();
        }
    }
}
