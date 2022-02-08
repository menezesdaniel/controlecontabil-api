package com.menezesdaniel.controlecontabil.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //mapeamento lombok
@AllArgsConstructor
@NoArgsConstructor
public class AtualizaStatusDto {
	
	private String status;
}
