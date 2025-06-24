; Código assembly gerado pelo compilador MACSLang

section .data

section .text
    global _start

_start:

    ; Saída do programa
    mov eax, 1      ; syscall exit
    xor ebx, ebx    ; código de retorno 0
    int 0x80        ; chamada de sistema
