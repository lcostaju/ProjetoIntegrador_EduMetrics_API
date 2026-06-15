package br.edu.iftm.edumetrics.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "desempenhos", indexes = {
    @Index(name = "idx_desemp_aluno", columnList = "aluno_id"),
    @Index(name = "idx_desemp_aluno_disc", columnList = "aluno_id, disciplina_id")
})
public class Desempenho {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(precision = 5, scale = 2)
    private BigDecimal nota1;

    @Column(precision = 5, scale = 2)
    private BigDecimal nota2;

    @Column(precision = 5, scale = 2)
    private BigDecimal notaFinal; // calculado: (nota1 + nota2) / 2

    @Column(nullable = false, length = 7)
    private String semestre; // ex: "2026/1"

    public Desempenho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }

    public BigDecimal getNota1() { return nota1; }
    public void setNota1(BigDecimal nota1) { this.nota1 = nota1; }

    public BigDecimal getNota2() { return nota2; }
    public void setNota2(BigDecimal nota2) { this.nota2 = nota2; }

    public BigDecimal getNotaFinal() { return notaFinal; }
    public void setNotaFinal(BigDecimal notaFinal) { this.notaFinal = notaFinal; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Desempenho that = (Desempenho) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}