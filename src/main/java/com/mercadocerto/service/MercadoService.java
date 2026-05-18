package com.mercadocerto.service;

import com.mercadocerto.model.Mercado;
import com.mercadocerto.repository.MercadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
 
@Service
public class MercadoService {

    private final MercadoRepository repo;

    @Value("${uploads.path:uploads}")
    private String uploadsPath;

    public MercadoService(MercadoRepository repo) {
        this.repo = repo;
    }
 
    public List<Mercado> listarTodos() {
        return repo.findAll();
    }
 
    public Optional<Mercado> buscarPorId(Integer id) {
        return repo.findById(id);
    }
 
    public Mercado salvar(Mercado mercado) {
        return repo.save(mercado);
    }
 
    public Mercado atualizar(Integer id, Mercado novo) {
        return repo.findById(id).map(m -> {
            if (novo.getNomeMercado() == null || novo.getNomeMercado().isBlank())
                throw new IllegalArgumentException("Nome do mercado é obrigatório");
            if (novo.getEndereco() == null || novo.getEndereco().isBlank())
                throw new IllegalArgumentException("Endereço é obrigatório");
            if (novo.getCidade() == null || novo.getCidade().isBlank())
                throw new IllegalArgumentException("Cidade é obrigatória");
            if (novo.getLatitude() == null || novo.getLatitude() < -90 || novo.getLatitude() > 90)
                throw new IllegalArgumentException("Latitude inválida (deve estar entre -90 e 90)");
            if (novo.getLongitude() == null || novo.getLongitude() < -180 || novo.getLongitude() > 180)
                throw new IllegalArgumentException("Longitude inválida (deve estar entre -180 e 180)");

            m.setNomeMercado(novo.getNomeMercado().trim());
            m.setEndereco(novo.getEndereco().trim());
            m.setCidade(novo.getCidade().trim());
            m.setLatitude(novo.getLatitude());
            m.setLongitude(novo.getLongitude());
            return repo.save(m);
        }).orElseThrow(() -> new RuntimeException("Mercado não encontrado: " + id));
    }
 
    public Mercado atualizarFoto(Integer id, MultipartFile foto) throws IOException {
        return repo.findById(id).map(m -> {
            try {
                m.setFoto(salvarFotoNoDisco(foto));
                return repo.save(m);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar foto", e);
            }
        }).orElseThrow(() -> new RuntimeException("Mercado não encontrado: " + id));
    }

    private String salvarFotoNoDisco(MultipartFile foto) throws IOException {
        Path dir = Paths.get(uploadsPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String original  = foto.getOriginalFilename();
        String nomeSeguro = "mercado_" + System.currentTimeMillis() + "_" +
                (original != null ? original.replaceAll("[^a-zA-Z0-9._-]", "_") : "foto");
        Files.copy(foto.getInputStream(), dir.resolve(nomeSeguro), StandardCopyOption.REPLACE_EXISTING);
        return nomeSeguro;
    }

    public void remover(Integer id) {
        repo.deleteById(id);
    }

    public List<Mercado> buscarProximos(double lat, double lng, double raioKm) {
        return repo.buscarPorProximidade(lat, lng, raioKm);
    }
}