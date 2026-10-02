package com.distribuidora.modelo;

public class Proveedor {

    private int id;
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;

    public Proveedor() {
    }

    // contacto, teléfono y email son opcionales y pueden ser null
    public Proveedor(String nombre, String contacto, String telefono, String email) {
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
    }

    public Proveedor(int id, String nombre, String contacto, String telefono, String email) {
        this(nombre, contacto, telefono, email);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Proveedor{id=" + id + ", nombre='" + nombre + "', contacto='" + contacto
                + "', telefono='" + telefono + "', email='" + email + "'}";
    }
}
