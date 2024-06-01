package Clases;

import Generics.GestorHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static Clases.Constantes.NOMBRE_ARCHIVO_LIBROS;

public class Biblioteca implements Serializable {

    private String nombreBiblioteca;
    private GestorHashMap<Integer, Libro> hashMapDeLibros;
    private GestorHashMap<Integer, Cliente> hashMapClientes;
    private GestorHashMap<Integer, RegistroAlquiler> hashMapAlquileres;

    public Biblioteca(String nombreBiblioteca) {
        this.nombreBiblioteca = nombreBiblioteca;
        this.hashMapClientes = new GestorHashMap<>();
        this.hashMapDeLibros = new GestorHashMap<>();
        this.hashMapAlquileres = new GestorHashMap<>();
    }

    public void agregarLibro(Integer ISBN, Libro libro) {
        hashMapDeLibros.agregar(ISBN, libro);
        guardarLibrosEnJSON(); // Llamar a la función para guardar en JSON
    }

    public HashMap<Integer, Cliente> getHashMapDeClientes() {
        return hashMapClientes.obtenerTodos();
    }

    public void agregarCopiaDeLibro(Integer ISBN) {
        Libro libro = hashMapDeLibros.buscar(ISBN);
        if (libro != null) {
            libro.agregarCopiaLibro();
            guardarLibrosEnJSON();
        }
    }

    public void guardarLibrosEnJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Libro libro : hashMapDeLibros.obtenerTodos().values()) {
            JSONObject jsonLibro = libro.toJson();
            jsonArray.put(jsonLibro);
        }
        JsonUtiles.grabar(jsonArray, NOMBRE_ARCHIVO_LIBROS);
    }



    public Libro buscarLibros(Integer ISBN) {
        return hashMapDeLibros.buscar(ISBN);
    }

    public void eliminarLibro(Integer ISBN) {
        hashMapDeLibros.eliminar(ISBN);
        guardarLibrosEnJSON(); // Asegurarse de actualizar el archivo JSON
    }

    public void agregarCliente(Integer idCliente, Cliente cliente) {
        hashMapClientes.agregar(idCliente, cliente);
    }

    public Cliente buscarCliente(Integer idCliente) {
        return hashMapClientes.buscar(idCliente);
    }

    public void eliminarCliente(Integer idCliente) {
        hashMapClientes.eliminar(idCliente);
    }

    public void agregarRegistro(RegistroAlquiler registro)
    {
        hashMapAlquileres.agregar(registro.getIdAlquiler(),registro);
    }

    public RegistroAlquiler buscarRegistro(Integer idAlquiler)
    {
        return hashMapAlquileres.buscar(idAlquiler);
    }

    public HashMap<Integer, Libro> getHashMapDeLibros() {
        return hashMapDeLibros.obtenerTodos();
    }

    public GestorHashMap<Integer, RegistroAlquiler> getHashMapAlquileres() {
        return hashMapAlquileres;
    }

    public void cargarLibrosDesdeJson(String archivoJson) {
        String contenido = JsonUtiles.leer(archivoJson);
        try {
            JSONArray jsonArray = new JSONArray(contenido);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Libro libro = Libro.fromJson(jsonObject);
                this.agregarLibro(libro.getISBN(), libro);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Carga todos los libros del hashmap al archivo
    public void cargarLibrosToJson(String archivoJson)
    {
        JSONArray jsonArray = new JSONArray();
        for (Libro libro: hashMapDeLibros.obtenerTodos().values())
        {
            jsonArray.put(libro.toJson());
        }
        JsonUtiles.grabar(jsonArray,archivoJson);
    }

    // Function to get all unique genres available in the library
    public ArrayList<String> obtenerGenerosDisponibles() {
        HashSet<String> generos = new HashSet<>();
        for (Libro libro : hashMapDeLibros.obtenerTodos().values()) {
            generos.add(libro.getGenero());
        }
        return new ArrayList<>(generos);
    }

    // Function to search books by author
    public ArrayList<Libro> buscarLibrosPorAutor(String autor) {
        ArrayList<Libro> librosPorAutor = new ArrayList<>();
        for (Libro libro : hashMapDeLibros.obtenerTodos().values()) {
            if (libro.getAutor().equalsIgnoreCase(autor)) {
                librosPorAutor.add(libro);
            }
        }
        return librosPorAutor;
    }


    public ArrayList<Libro> buscarLibrosPorGenero(String genero) {
        ArrayList<Libro> librosXgenero = new ArrayList<>();
        for (Libro libro : hashMapDeLibros.obtenerTodos().values()) {
            if (libro.getGenero().equalsIgnoreCase(genero)) {
                librosXgenero.add(libro);
            }
        }
        return librosXgenero;
    }

    public void cargarClientesDesdeJson(String archivoJson) {
        String contenido = JsonUtiles.leer(archivoJson);
        try {
            JSONObject jsonObject = new JSONObject(contenido);
            JSONArray jsonArray = jsonObject.getJSONArray("clientes");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject clienteObj = jsonArray.getJSONObject(i);
                Cliente cliente = Cliente.fromJson(clienteObj);
                this.agregarCliente(cliente.getIdCliente(), cliente);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargarClientesToJson (String archivoJson) //Carga todos los clientes que contiene el hashMap al archivo JSON
    {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Cliente cliente: hashMapClientes.obtenerTodos().values())
            {
                jsonArray.put(cliente.toJson());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientes",jsonArray);
            JsonUtiles.grabar(jsonObject,archivoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void cargarRegistroAlquileresToJson (String archivoJson) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (RegistroAlquiler registroAlquiler : hashMapAlquileres.obtenerTodos().values())
        {
            int idAlquiler = registroAlquiler.getIdAlquiler();
            JSONObject jsonLibro=registroAlquiler.getLibroAlquilado().toJson();
            JSONObject jsonCliente = registroAlquiler.getCliente().toJson();
            String fechaAlquiler = registroAlquiler.getFechaAlquiler();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idAlquiler",idAlquiler);
            jsonObject.put("libroAlquilado",jsonLibro);
            jsonObject.put("cliente",jsonCliente);
            jsonObject.put("fechaAlquiler",fechaAlquiler);
            jsonArray.put(jsonObject);
        }
        JsonUtiles.grabar(jsonArray,archivoJson);
    }

    public void cargarRegistroAlquilerDesdeJson(String archivoJson) throws JSONException {
        String contenido = JsonUtiles.leer(archivoJson);
        JSONArray jsonArray = new JSONArray(contenido);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int idAlquiler=jsonObject.getInt("idAlquiler");
            Libro libro = Libro.fromJson(jsonObject.getJSONObject("libroAlquilado"));
            Cliente cliente = Cliente.fromJson(jsonObject.getJSONObject("cliente"));
            String fechaAlquiler = jsonObject.getString("fechaAlquiler");
            this.agregarRegistro(new RegistroAlquiler(idAlquiler,libro,cliente,fechaAlquiler));
        }
    }
}