package com.bcopstein.voos.adapter.in.rest.erro;

import com.bcopstein.voos.application.exception.ConflitoDeReservaException;
import com.bcopstein.voos.application.exception.PlanoInvalidoException;
import com.bcopstein.voos.application.exception.RecursoNaoEncontradoException;
import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduz as exceções das camadas internas em respostas HTTP:
 * 400 dados malformados, 404 recurso inexistente, 409 operação não permitida na situação
 * atual ou conflito de reserva, 422 plano que não passou na validação.
 */
@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroResponse> dadosInvalidos(DadosInvalidosException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage(), List.of());
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ResponseEntity<ErroResponse> operacaoNaoPermitida(OperacaoNaoPermitidaException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), List.of());
    }

    @ExceptionHandler(ConflitoDeReservaException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoDeReservaException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), List.of());
    }

    @ExceptionHandler(PlanoInvalidoException.class)
    public ResponseEntity<ErroResponse> planoInvalido(PlanoInvalidoException e) {
        return resposta(HttpStatus.valueOf(422), e.getMessage(), e.getErros());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> corpoInvalido(MethodArgumentNotValidException e) {
        List<String> detalhes = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).toList();
        return resposta(HttpStatus.BAD_REQUEST, "Requisição com campos inválidos.", detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoIlegivel(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ilegível. Datas devem seguir AAAA-MM-DDTHH:mm:ss e tipoPassagem deve ser SOBREVOO ou POUSO.",
                List.of());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResponse> parametroInvalido(Exception e) {
        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro ausente ou inválido: " + e.getMessage(), List.of());
    }

    private static ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status)
                .body(new ErroResponse(status.value(), status.getReasonPhrase(), mensagem, detalhes));
    }
}
