package com.mercadocerto.model;
 
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
 
@Entity
@Table(name = "usuario")
public class Usuario {
 
    public enum TipoConta { USUARIO, COMERCIO, ADMIN }
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
 
    @Column(name = "nome_usuario", nullable = false)
    private String nomeUsuario;
 
    @Column(unique = true, nullable = false)
    private String email;
 
    @Column(unique = true)
    private String login;
 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String senha;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false)
    private TipoConta tipoConta = TipoConta.USUARIO;
 
    @Column(unique = true)
    private String cnpj;
 
    public Usuario() {}
 
    public Integer   getIdUsuario()              { return idUsuario; }
    public void      setIdUsuario(Integer v)     { this.idUsuario = v; }
    public String    getNomeUsuario()            { return nomeUsuario; }
    public void      setNomeUsuario(String v)    { this.nomeUsuario = v; }
    public String    getEmail()                  { return email; }
    public void      setEmail(String v)          { this.email = v; }
    public String    getLogin()                  { return login; }
    public void      setLogin(String v)          { this.login = v; }
    public String    getSenha()                  { return senha; }
    public void      setSenha(String v)          { this.senha = v; }
    public TipoConta getTipoConta()              { return tipoConta; }
    public void      setTipoConta(TipoConta v)   { this.tipoConta = v; }
    public String    getCnpj()                   { return cnpj; }
    public void      setCnpj(String v)           { this.cnpj = v; }
    public boolean   isComercio()                { return TipoConta.COMERCIO.equals(tipoConta); }
}