package gaian.svsa.ct.service.pdf;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import gaian.svsa.ct.modelo.Denuncia;
import gaian.svsa.ct.modelo.Pessoa;
import gaian.svsa.ct.util.DateUtils;
import gaian.svsa.ct.util.NegocioException;

public class DenunciaPDFService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(AtestadoPDFService.class);
	
	/*
	 * Emissão da Denúncia
	 * 
	 */
	
	// para stream e impressão de Emissão da Denúncia
	public ByteArrayOutputStream generateStream(Denuncia denuncia, String s3Key, String secretaria) throws NegocioException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			PdfWriter writer = new PdfWriter(baos);
	        // Creating a PdfDocument  
	        PdfDocument pdf = new PdfDocument(writer);	        
	        // Creating a Document   
	        Document document = new Document(pdf); 		        
	        
			document.setMargins(0, 50, 50, 50);

	        
	        // gera impressão da Denúncia
	        generateContent(document, denuncia, s3Key, secretaria);
	        
	        return baos;
	        
		}catch (Exception ex) {
	    	log.error("error: " + ex.getMessage());	    	
	    	throw new NegocioException("Erro na montagem do PDF: " + ex.getMessage());
	    }
	}
	
	// para download de Emissão de Atestado
	public void generatePDF(String dest, String nome, Denuncia denuncia, String s3Key, String secretaria) throws Exception {
		
		// Creating a PdfWriter			  
		PdfWriter writer = new 
		PdfWriter(dest);		  
		// Creating a PdfDocument       
		PdfDocument pdf = new PdfDocument(writer);
		// Adding an empty page 
        pdf.addNewPage();        
		// Creating a Document
		Document document = new Document(pdf);		
		
		document.setMargins(50, 50, 50, 50);		
		
		generateContent(document, denuncia, s3Key, secretaria);
		
	}

		
	private void generateContent(Document document, Denuncia denuncia, String s3Key, String secretaria)
			throws Exception {

		PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
		TextAlignment align = TextAlignment.JUSTIFIED;
		// Header

		headerAtestado(document, denuncia.getConselheiro().getUnidade().getNome(), denuncia, secretaria);

		Paragraph line1 = new Paragraph(
				"\nREGISTRO DE DENÚNCIA		DATA: " + DateUtils.parseDateToString(denuncia.getDataEmissao())
						+ "					CODIGO: " + denuncia.getCodigo());
		line1.setFontSize(15);
		line1.setBold();
		line1.setUnderline();
		line1.setFont(font);
		line1.setTextAlignment(align);
		document.add(line1);

		Paragraph line2 = new Paragraph("Nome do Responsável: " + denuncia.getFamilia().getPessoaReferencia().getNome()
				+ "\n Conselheiro de Referência da Denuncia: " + denuncia.getConselheiroReferencia().getNome() 
				+ "\n Endereço: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getEndereco() 
				+ " Nº: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getNumero()
				+ "\n Bairro: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getBairro()
				+ "\n Complemento: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getComplemento()
				+ "\n Referencia: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getReferencia()
				+ "\n CEP: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getCep() 		
				+ "\n Cidade: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getMunicipio() 
				+ " UF: " + denuncia.getFamilia().getPessoaReferencia().getEndereco().getUf()
				+ "\n Telefone: " + denuncia.getFamilia().getPessoaReferencia().getTelefone());	
		line2.setFontSize(12);
		line2.setFont(font);
		line2.setTextAlignment(align);
		document.add(line2);

		Paragraph line3 = new Paragraph("MEMBROS DA FAMILIA:");
		line3.setBold();
		line3.setFontSize(14);
		line3.setFont(font);
		line3.setTextAlignment(align);
		document.add(line3);

		for (Pessoa p : denuncia.getFamilia().getMembros()) {

			// PARA NÃO EMITIR PESSOAS EXCLUIDAS NA DENUNCIA
			if (p.getExcluida() == true) {

				Paragraph line4 = new Paragraph();
				line4.setFontSize(12);
				line4.setFont(font);
				line4.setTextAlignment(align);
				document.add(line4);

				Paragraph line5 = new Paragraph();
				line5.setFontSize(12);
				line5.setFont(font);
				line5.setTextAlignment(align);
				document.add(line5);

			} else {
				// Emissão Normal
				Paragraph line4 = new Paragraph("Nome: " + p.getNome() + "\n" + "Data de Nascimento: "
						+ DateUtils.parseDateToString(p.getDataNascimento()) + "\n Parentesco:" + p.getParentesco()
						+ "\n Telefone de contato: " + p.getTelefone() 
						+ "\n Endereço: " + p.getEndereco().getEndereco() + " Nº: " + p.getEndereco().getNumero()
						+ "\n Bairro: " + p.getEndereco().getBairro()
						+ "\n Complemento: " + p.getEndereco().getComplemento()
						+ "\n Referencia: " + p.getEndereco().getReferencia()
						+ "\n CEP: " + p.getEndereco().getCep()
						+ "\n Cidade: " + p.getEndereco().getMunicipio() + " UF: " + p.getEndereco().getUf());
				line4.setFontSize(12);
				line4.setFont(font);
				line4.setTextAlignment(align);
				document.add(line4);

				// Condicional para não emitir o campo escola, série e periodo caso o valor de
				// escola seja nulo.
				//
				if (p.getEscola() != null) {
					Paragraph line5 = new Paragraph(
							"Escola: " + p.getEscola() + "\nSérie: " + p.getSerie() + "\nPeriodo: " + p.getPeriodo());
					line5.setFontSize(12);
					line5.setFont(font);
					line5.setTextAlignment(align);
					document.add(line5);

				} else {
					Paragraph line5 = new Paragraph();
					line5.setFontSize(12);
					line5.setFont(font);
					line5.setTextAlignment(align);
					document.add(line5);
				}

				Paragraph line6 = new Paragraph(
						"Conselheiro de Responsável da Pessoa: " + p.getConselheiroResponsavel().getNome());
				line6.setFontSize(12);
				line6.setFont(font);
				line6.setTextAlignment(align);
				document.add(line6);
				
			
				if (p.getResponsavel() != null) {
					Paragraph line9 = new Paragraph("Responsavel Legal da Criança: " + p.getResponsavel().getNome());
					line9.setFontSize(12);
					line9.setFont(font);
					line9.setTextAlignment(align);
					document.add(line9);
				} else {
					Paragraph line9 = new Paragraph();
					line9.setFontSize(12);
					line9.setFont(font);
					line9.setTextAlignment(align);
					document.add(line9);
				}
				// Linha que separa as pessoas
				Paragraph line7 = new Paragraph("____________________________________________________________");
				line7.setFontSize(14);
				line7.setFont(font);
				line7.setTextAlignment(align);
				document.add(line7);

			}

		}
		// Campo de relato da denuncia
		Paragraph line8 = new Paragraph("Relato da Denuncia: " + denuncia.getRelato());
		line8.setFontSize(12);
		line8.setFont(font);
		line8.setTextAlignment(align);
		document.add(line8);

		document.close();
	}

	private void headerAtestado(Document document, String unidade, Denuncia denuncia, String secretaria) throws Exception {
		try {
			PdfFont fontTitulo = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			log.info(unidade);
				Paragraph titulo = new Paragraph(
						"Conselho Tutelar de Salto");
			//	titulo.setBackgroundColor(ColorConstants.BLUE);
				titulo.setFontSize(24);
				titulo.setFont(fontTitulo);
				titulo.setTextAlignment(TextAlignment.CENTER);
				titulo.getMarginTop();
				document.add(titulo);
				
				Paragraph titulo2 = new Paragraph("Lei Federal nº 8.069, de 13 de julho de 1990\n" 
				+ denuncia.getUnidade().getEndereco()
						+ "\nFone: " + denuncia.getUnidade().getEndereco().getTelefoneContato() 
						+ "\n");
			//	titulo2.setBackgroundColor(ColorConstants.BLUE);
				titulo2.setFontSize(12);
				titulo2.setFont(fontTitulo);
				titulo2.setTextAlignment(TextAlignment.CENTER);
				titulo2.getMarginTop();
				document.add(titulo2);
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Não foi possivel localizar a unidade.");
		}
	}
}
