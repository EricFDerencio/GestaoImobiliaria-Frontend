package com.imobiliaria.frontend.controller;

import com.imobiliaria.frontend.exception.BackendIndisponivelException;
import com.imobiliaria.frontend.exception.DadosRejeitadosException;
import com.imobiliaria.frontend.model.Imovel;
import com.imobiliaria.frontend.service.ImovelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller MVC. Fluxo: o POST processa o cadastro e redireciona (padrão
 * Post/Redirect/Get); as mensagens viajam como "flash attributes".
 */
@Controller
public class ImovelController {

    private final ImovelService service;

    public ImovelController(ImovelService service) {
        this.service = service;
    }

    // Opções do <select>. Quem valida de verdade é o backend.
    @ModelAttribute("tipos")
    public List<String> tipos() {
        return List.of("Casa", "Apartamento", "Terreno");
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/imoveis";
    }

    @GetMapping("/imoveis")
    public String listar(Model model) {
        // Se veio de um cadastro com erro, o formulário já chega preenchido (flash "imovel")
        if (!model.containsAttribute("imovel")) {
            model.addAttribute("imovel", new Imovel());
        }
        try {
            model.addAttribute("imoveis", service.listar());
        } catch (BackendIndisponivelException e) {
            model.addAttribute("imoveis", List.of());
            // Não sobrescreve um erro de cadastro que já esteja sendo exibido
            if (!model.containsAttribute("erro")) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        return "imoveis";
    }

    @PostMapping("/imoveis")
    public String cadastrar(@ModelAttribute("imovel") Imovel imovel,
                            BindingResult binding,
                            RedirectAttributes redirect) {
        // Ex.: valor com formato que não é número
        if (binding.hasErrors()) {
            redirect.addFlashAttribute("erro", "Há campos com formato inválido. Confira o valor informado.");
            redirect.addFlashAttribute("imovel", imovel);
            return "redirect:/imoveis";
        }
        try {
            service.cadastrar(imovel);
            redirect.addFlashAttribute("sucesso", "Imóvel cadastrado com sucesso!");
        } catch (DadosRejeitadosException e) {
            redirect.addFlashAttribute("erro", e.getMessage() + ". Corrija os campos abaixo e tente novamente.");
            redirect.addFlashAttribute("detalhes", e.getDetalhes());
            redirect.addFlashAttribute("imovel", imovel);
        } catch (BackendIndisponivelException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            redirect.addFlashAttribute("imovel", imovel);
        }
        return "redirect:/imoveis";
    }
}
