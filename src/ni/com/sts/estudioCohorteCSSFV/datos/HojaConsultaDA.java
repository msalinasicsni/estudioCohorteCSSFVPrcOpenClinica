package ni.com.sts.estudioCohorteCSSFV.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;

import ni.com.sts.estudioCohorteCSSFV.modelo.HojaConsulta;
import ni.com.sts.estudioCohorteCSSFV.servicios.HojaConsultaService;
import ni.com.sts.estudioCohorteCSSFV.util.ConnectionDAO;
import ni.com.sts.estudioCohorteCSSFV.util.UtilDate;
import ni.com.sts.estudioCohorteCSSFV.util.UtilLog;
import ni.com.sts.estudioCohorteCSSFV.util.UtilProperty;

public class HojaConsultaDA extends ConnectionDAO implements HojaConsultaService {

	private final Logger logger = Logger.getLogger(this.getClass());
	private Connection conn=null;
	private PreparedStatement  pstm = null;
	private CompositeConfiguration config;
	
	public HojaConsultaDA(){
		config = UtilProperty.getConfigurationfromExternalFile("estudioCohorteCSSFVOPC.properties");
		UtilLog.setLog(config.getString("estudioCohorteCSSFVOPC.log"));
	}
	
	@Override
	public List<HojaConsulta> getHojasConsultaPendientesCarga() throws Exception {
		conn = getConection();
		List<HojaConsulta> resultado = new ArrayList<HojaConsulta>();
		ResultSet rs = null;
		try{
			String query = "select   sec_hoja_consulta,  cod_expediente,  num_hoja_consulta,  to_char(fecha_consulta, 'dd/MM/yyyy HH24:MI:SS'),  peso_kg,  talla_cm, temperaturac,  0,  fcia_resp,  fcia_card,  lugar_atencion,  consulta,  seg_chick,  tem_medc,  ult_dia_fiebre,  ult_dosis_antipiretico,  fiebre, "+
  "astenia,  asomnoliento,  mal_estado,  perdida_consciencia,  inquieto,  convulsiones,  hipotermia,  letargia,  cefalea, cefalea,  rigidez_cuello,  inyeccion_conjuntival, "+
  "hemorragia_suconjuntival,  dolor_retroocular,  fontanela_abombada,  ictericia_conuntival,  eritema,  dolor_garganta,  adenopatias_cervicales,  exudado, "+
  "petequias_mucosa,  tos,  rinorrea,  congestion_nasal,  otalgia,  aleteo_nasal,  apnea,  respiracion_rapida,  quejido_espiratorio,  estirador_reposo, "+
  "tiraje_subcostal,  sibilancias,  crepitos,  roncos,  otra_fif,  nueva_fif,  poco_apetito,  nausea,  dificultad_alimentarse,  vomito_12horas,  diarrea,  diarrea_sangre, "+
  "estrenimiento,  dolor_ab_intermitente,  dolor_ab_continuo,  epigastralgia,  intolerancia_oral,  distension_abdominal,  hepatomegalia,  lengua_mucosas_secas,  pliegue_cutaneo, "+
  "orina_reducida,  bebe_con_sed,  ojos_hundidos,  fontanela_hundida,  sintomas_urinarios,  leucocituria,  nitritos,  bilirrubinuria,  altralgia,  mialgia,  lumbalgia, "+
  "dolor_cuello,  tenosinovitis,  artralgia_proximal,  artralgia_distal,  conjuntivitis,  edema_munecas,  edema_codos,  edema_hombros,  edema_rodillas,  edema_tobillos, "+
  "rahs_localizado,  rahs_generalizado,  rash_eritematoso,  rahs_macular,  rash_papular,  rahs_moteada,  rubor_facial,  equimosis,  cianosis_central,  ictericia,  obeso, "+
  "sobrepeso,  sospecha_problema,  normal,  bajo_peso,  bajo_peso_severo,  lactancia_materna,  vacunas_completas,  vacuna_influenza,  fecha_vacuna,  interconsulta_pediatrica, "+
  "referencia_hospital,  referencia_dengue,  referencia_irag,  referencia_chik,  eti,  irag,  neumonia,  saturaciono2,  imc,  categoria,  cambio_categoria,  manifestacion_hemorragica, "+
  "prueba_torniquete_positiva,  petequia_10_pt,  petequia_20_pt,  piel_extremidades_frias,  palidez_en_extremidades,  epistaxis,  gingivorragia,  petequias_espontaneas,  llenado_capilar_2seg, "+
  "cianosis,  linfocitosa_atipicos,  hipermenorrea,  hematemesis,  melena,  hemoconcentracion,  hospitalizado,  hospitalizado_especificar,  transfusion_sangre, "+
  "transfusion_especificar,  tomando_medicamento,  medicamento_especificar,  medicamento_distinto,  medicamento_dist_especificar,  bhc,  serologia_dengue,  serologia_chik, "+
  "gota_gruesa,  extendido_periferico,  ego,  egh,  citologia_fecal,  factor_reumatoideo,  albumina,  ast_alt,  bilirrubinas,  cpk,  colesterol,  influenza,  otro_examen_lab, "+
  "num_orden_laboratorio,  acetaminofen,  asa,  ibuprofen,  penicilina,  amoxicilina,  dicloxacilina,  otro_antibiotico,  furazolidona,  metronidazol_tinidazol,  albendazol_mebendazol, "+
  "sulfato_ferroso,  suero_oral,  sulfato_zinc,  liquidos_iv,  prednisona,  hidrocortisona_iv,  salbutamol,  oseltamivir,  diagnostico1,  diagnostico2,  diagnostico3, "+
  "diagnostico4,  otro_diagnostico,  proxima_cita,  am_pm_ult_dia_fiebre,  to_char(hora_ult_dosis_antipiretico, 'dd/MM/yyyy HH12:MI:SS'), "+
  "am_pm_ult_dosis_antipiretico, fis,  fif,  hepatomegalia_cm,  eritrocitos, fecha_linfocitos, fecha_cierre, fecha_cierre_cambio_turno, usuario_medico, usuario_enfermeria, turno, " +
  "horario_clases, otro, pad, pas, telef, hemoconc, vomito12h, oel, hora, horasv " +
					" from hoja_consulta "+
							"where estado = ? "+
							"and (estado_carga = ? or estado_carga is null) order by sec_hoja_consulta";

			pstm = conn.prepareStatement(query);
			pstm.setString(1, "7"); //hoja_consulta.estado = cerrado
			pstm.setString(2, "0"); //hoja_consulta.estado_carga = Pendiente
			rs = pstm.executeQuery();
			while (rs.next()){
				HojaConsulta dato = new HojaConsulta();
				dato.setSecHojaConsulta(rs.getInt(1));
				dato.setCodExpediente(rs.getInt(2));
				dato.setNumHojaConsulta(rs.getInt(3));
				dato.setFechaConsulta(UtilDate.StringToDate(rs.getString(4),"dd/MM/yyyy HH:mm:ss"));
				logger.debug("");
				if (rs.getBigDecimal(5)!=null) dato.setPesoKg(rs.getBigDecimal(5));
				if (rs.getBigDecimal(6)!=null) dato.setTallaCm(rs.getBigDecimal(6));
				if (rs.getBigDecimal(7)!=null) dato.setTemperaturac(rs.getBigDecimal(7));
				//dato.setPresion(rs.getInt(8));
				dato.setFciaResp(rs.getShort(9));
				dato.setFciaCard(rs.getShort(10));
				if (rs.getString(11)!=null) dato.setLugarAtencion(rs.getString(11));
				if (rs.getString(12)!=null) dato.setConsulta(rs.getString(12));
				if (rs.getString(13)!=null) dato.setSegChick(rs.getString(13).charAt(0));
				if (rs.getBigDecimal(14)!=null) dato.setTemMedc(rs.getBigDecimal(14));
				if (rs.getDate(15)!=null) dato.setUltDiaFiebre(rs.getDate(15));
				if (rs.getDate(16)!=null) dato.setUltDosisAntipiretico(rs.getDate(16));
				if (rs.getString(17)!=null) dato.setFiebre(rs.getString(17).charAt(0));
				if (rs.getString(18)!=null) dato.setAstenia(rs.getString(18).charAt(0));
				if (rs.getString(19)!=null) dato.setAsomnoliento(rs.getString(19).charAt(0));
				if (rs.getString(20)!=null) dato.setMalEstado(rs.getString(20).charAt(0));
				if (rs.getString(21)!=null) dato.setPerdidaConsciencia(rs.getString(21).charAt(0));
				if (rs.getString(22)!=null) dato.setInquieto(rs.getString(22).charAt(0));
				if (rs.getString(23)!=null) dato.setConvulsiones(rs.getString(23).charAt(0));
				if (rs.getString(24)!=null) dato.setHipotermia(rs.getString(24).charAt(0));
				if (rs.getString(25)!=null) dato.setLetargia(rs.getString(25).charAt(0));
				if (rs.getString(26)!=null) dato.setCefalea(rs.getString(26).charAt(0));
				if (rs.getString(27)!=null) dato.setCefalea(rs.getString(27).charAt(0));
				if (rs.getString(28)!=null) dato.setRigidezCuello(rs.getString(28).charAt(0));
				if (rs.getString(29)!=null) dato.setInyeccionConjuntival(rs.getString(29).charAt(0));
				if (rs.getString(30)!=null) dato.setHemorragiaSuconjuntival(rs.getString(30).charAt(0));
				if (rs.getString(31)!=null) dato.setDolorRetroocular(rs.getString(31).charAt(0));
				if (rs.getString(32)!=null) dato.setFontanelaAbombada(rs.getString(32).charAt(0));
				if (rs.getString(33)!=null) dato.setIctericiaConuntival(rs.getString(33).charAt(0));
				if (rs.getString(34)!=null) dato.setEritema(rs.getString(34).charAt(0));
				if (rs.getString(35)!=null) dato.setDolorGarganta(rs.getString(35).charAt(0));
				if (rs.getString(36)!=null) dato.setAdenopatiasCervicales(rs.getString(36).charAt(0));
				if (rs.getString(37)!=null) dato.setExudado(rs.getString(37).charAt(0));
				if (rs.getString(38)!=null) dato.setPetequiasMucosa(rs.getString(38).charAt(0));
				if (rs.getString(39)!=null) dato.setTos(rs.getString(39).charAt(0));
				if (rs.getString(40)!=null) dato.setRinorrea(rs.getString(40).charAt(0));
				if (rs.getString(41)!=null) dato.setCongestionNasal(rs.getString(41).charAt(0));
				if (rs.getString(42)!=null) dato.setOtalgia(rs.getString(42).charAt(0));
				if (rs.getString(43)!=null) dato.setAleteoNasal(rs.getString(43).charAt(0));
				if (rs.getString(44)!=null) dato.setApnea(rs.getString(44).charAt(0));
				if (rs.getString(45)!=null) dato.setRespiracionRapida(rs.getString(45).charAt(0));
				if (rs.getString(46)!=null) dato.setQuejidoEspiratorio(rs.getString(46).charAt(0));
				if (rs.getString(47)!=null) dato.setEstiradorReposo(rs.getString(47).charAt(0));
				if (rs.getString(48)!=null) dato.setTirajeSubcostal(rs.getString(48).charAt(0));
				if (rs.getString(48)!=null) dato.setSibilancias(rs.getString(49).charAt(0));
				if (rs.getString(50)!=null) dato.setCrepitos(rs.getString(50).charAt(0));
				if (rs.getString(51)!=null) dato.setRoncos(rs.getString(51).charAt(0));
				if (rs.getString(52)!=null) dato.setOtraFif(rs.getString(52).charAt(0));
				if (rs.getDate(53)!=null) dato.setNuevaFif(rs.getDate(53));
				if (rs.getString(54)!=null) dato.setPocoApetito(rs.getString(54).charAt(0));
				if (rs.getString(55)!=null) dato.setNausea(rs.getString(55).charAt(0));
				if (rs.getString(56)!=null) dato.setDificultadAlimentarse(rs.getString(56).charAt(0));
				if (rs.getString(57)!=null) dato.setVomito12horas(rs.getString(57).charAt(0));
				if (rs.getString(58)!=null) dato.setDiarrea(rs.getString(58).charAt(0));
				if (rs.getString(59)!=null) dato.setDiarreaSangre(rs.getString(59).charAt(0));
				if (rs.getString(60)!=null) dato.setEstrenimiento(rs.getString(60).charAt(0));
				if (rs.getString(61)!=null) dato.setDolorAbIntermitente(rs.getString(61).charAt(0));
				if (rs.getString(62)!=null) dato.setDolorAbContinuo(rs.getString(62).charAt(0));
				if (rs.getString(63)!=null) dato.setEpigastralgia(rs.getString(63).charAt(0));
				if (rs.getString(64)!=null) dato.setIntoleranciaOral(rs.getString(64).charAt(0));
				if (rs.getString(65)!=null) dato.setDistensionAbdominal(rs.getString(65).charAt(0));
				if (rs.getString(66)!=null) dato.setHepatomegalia(rs.getString(66).charAt(0));
				if (rs.getString(67)!=null) dato.setLenguaMucosasSecas(rs.getString(67).charAt(0));
				if (rs.getString(68)!=null) dato.setPliegueCutaneo(rs.getString(68).charAt(0));
				if (rs.getString(69)!=null) dato.setOrinaReducida(rs.getString(69).charAt(0));
				if (rs.getString(70)!=null) dato.setBebeConSed(rs.getString(70).charAt(0));
				if (rs.getString(71)!=null) dato.setOjosHundidos(rs.getString(71));
				if (rs.getString(72)!=null) dato.setFontanelaHundida(rs.getString(72).charAt(0));
				if (rs.getString(73)!=null) dato.setSintomasUrinarios(rs.getString(73).charAt(0));
				if (rs.getString(74)!=null) dato.setLeucocituria(rs.getString(74).charAt(0));
				if (rs.getString(75)!=null) dato.setNitritos(rs.getString(75).charAt(0));
				if (rs.getString(76)!=null) dato.setBilirrubinuria(rs.getString(76).charAt(0));
				if (rs.getString(77)!=null) dato.setAltralgia(rs.getString(77).charAt(0));
				if (rs.getString(78)!=null) dato.setMialgia(rs.getString(78).charAt(0));
				if (rs.getString(79)!=null) dato.setLumbalgia(rs.getString(79).charAt(0));
				if (rs.getString(80)!=null) dato.setDolorCuello(rs.getString(80).charAt(0));
				if (rs.getString(81)!=null) dato.setTenosinovitis(rs.getString(81).charAt(0));
				if (rs.getString(82)!=null) dato.setArtralgiaProximal(rs.getString(82).charAt(0));
				if (rs.getString(83)!=null) dato.setArtralgiaDistal(rs.getString(83).charAt(0));
				if (rs.getString(84)!=null) dato.setConjuntivitis(rs.getString(84).charAt(0));
				if (rs.getString(85)!=null) dato.setEdemaMunecas(rs.getString(85).charAt(0));
				if (rs.getString(86)!=null) dato.setEdemaCodos(rs.getString(86).charAt(0));
				if (rs.getString(87)!=null) dato.setEdemaHombros(rs.getString(87).charAt(0));
				if (rs.getString(88)!=null) dato.setEdemaRodillas(rs.getString(88).charAt(0));
				if (rs.getString(89)!=null) dato.setEdemaTobillos(rs.getString(89).charAt(0));
				if (rs.getString(90)!=null) dato.setRahsLocalizado(rs.getString(90).charAt(0));
				if (rs.getString(91)!=null) dato.setRahsGeneralizado(rs.getString(91).charAt(0));
				if (rs.getString(92)!=null) dato.setRashEritematoso(rs.getString(92).charAt(0));
				if (rs.getString(93)!=null) dato.setRahsMacular(rs.getString(93).charAt(0));
				if (rs.getString(94)!=null) dato.setRashPapular(rs.getString(94).charAt(0));
				if (rs.getString(95)!=null) dato.setRahsMoteada(rs.getString(95).charAt(0));
				if (rs.getString(96)!=null) dato.setRuborFacial(rs.getString(96).charAt(0));
				if (rs.getString(97)!=null) dato.setEquimosis(rs.getString(97).charAt(0));
				if (rs.getString(98)!=null) dato.setCianosisCentral(rs.getString(98).charAt(0));
				if (rs.getString(99)!=null) dato.setIctericia(rs.getString(99).charAt(0));
				if (rs.getString(100)!=null) dato.setObeso(rs.getString(100).charAt(0));
				if (rs.getString(101)!=null) dato.setSobrepeso(rs.getString(101).charAt(0));
				if (rs.getString(102)!=null) dato.setSospechaProblema(rs.getString(102).charAt(0));
				if (rs.getString(103)!=null) dato.setNormal(rs.getString(103).charAt(0));
				if (rs.getString(104)!=null) dato.setBajoPeso(rs.getString(104).charAt(0));
				if (rs.getString(105)!=null) dato.setBajoPesoSevero(rs.getString(105).charAt(0));
				if (rs.getString(106)!=null) dato.setLactanciaMaterna(rs.getString(106).charAt(0));
				if (rs.getString(107)!=null) dato.setVacunasCompletas(rs.getString(107).charAt(0));
				if (rs.getString(108)!=null) dato.setVacunaInfluenza(rs.getString(108).charAt(0));
				if (rs.getDate(109)!=null) dato.setFechaVacuna(rs.getDate(109));
				if (rs.getString(110)!=null) dato.setInterconsultaPediatrica(rs.getString(110).charAt(0));
				if (rs.getString(111)!=null) dato.setReferenciaHospital(rs.getString(111).charAt(0));
				if (rs.getString(112)!=null) dato.setReferenciaDengue(rs.getString(112).charAt(0));
				if (rs.getString(113)!=null) dato.setReferenciaIrag(rs.getString(113).charAt(0));
				if (rs.getString(114)!=null) dato.setReferenciaChik(rs.getString(114).charAt(0));
				if (rs.getString(115)!=null) dato.setEti(rs.getString(115).charAt(0));
				if (rs.getString(116)!=null) dato.setIrag(rs.getString(116).charAt(0));
				if (rs.getString(117)!=null) dato.setNeumonia(rs.getString(117).charAt(0));				
				dato.setSaturaciono2(rs.getShort(118));
				if (rs.getBigDecimal(119)!=null) dato.setImc(rs.getBigDecimal(119));
				if (rs.getString(120)!=null) dato.setCategoria(rs.getString(120));
				if (rs.getString(121)!=null) dato.setCambioCategoria(rs.getString(121).charAt(0));
				if (rs.getString(122)!=null)
					dato.setManifestacionHemorragica(rs.getString(122).charAt(0));
					if (rs.getString(123)!=null)
					dato.setPruebaTorniquetePositiva(rs.getString(123).charAt(0));
					if (rs.getString(124)!=null)
						dato.setPetequia10Pt(rs.getString(124).charAt(0));
					if (rs.getString(125)!=null)
						dato.setPetequia20Pt(rs.getString(125).charAt(0));
					if (rs.getString(126)!=null)
						dato.setPielExtremidadesFrias(rs.getString(126).charAt(0));
					if (rs.getString(127)!=null)
						dato.setPalidezEnExtremidades(rs.getString(127).charAt(0));
					if (rs.getString(128)!=null)
						dato.setEpistaxis(rs.getString(128).charAt(0));
					if (rs.getString(129)!=null)
						dato.setGingivorragia(rs.getString(129).charAt(0));
					if (rs.getString(130)!=null)
						dato.setPetequiasEspontaneas(rs.getString(130).charAt(0));
					if (rs.getString(131)!=null)
						dato.setLlenadoCapilar2seg(rs.getString(131).charAt(0));
					if (rs.getString(132)!=null)
						dato.setCianosis(rs.getString(132).charAt(0));
					if (rs.getBigDecimal(133)!=null)
						dato.setLinfocitosaAtipicos(rs.getBigDecimal(133));
					if (rs.getString(134)!=null)
						dato.setHipermenorrea(rs.getString(134).charAt(0));
					if (rs.getString(135)!=null)
						dato.setHematemesis(rs.getString(135).charAt(0));
					if (rs.getString(136)!=null)
						dato.setMelena(rs.getString(136).charAt(0));
					dato.setHemoconcentracion(rs.getShort(137));
					if (rs.getString(138)!=null)
						dato.setHospitalizado(rs.getString(138).charAt(0));
					if (rs.getString(139)!=null)
						dato.setHospitalizadoEspecificar(rs.getString(139));
					if (rs.getString(140)!=null)
						dato.setTransfusionSangre(rs.getString(140).charAt(0));
					if (rs.getString(141)!=null)
						dato.setTransfusionEspecificar(rs.getString(141));
					if (rs.getString(142)!=null)
						dato.setTomandoMedicamento(rs.getString(142).charAt(0));
					if (rs.getString(143)!=null)
						dato.setMedicamentoEspecificar(rs.getString(143));
					if (rs.getString(144)!=null)
						dato.setMedicamentoDistinto(rs.getString(144).charAt(0));
					if (rs.getString(145)!=null)
						dato.setMedicamentoDistEspecificar(rs.getString(145));
					if (rs.getString(146)!=null)
						dato.setBhc(rs.getString(146).charAt(0));
					if (rs.getString(147)!=null)
						dato.setSerologiaDengue(rs.getString(147).charAt(0));
					if (rs.getString(148)!=null)
						dato.setSerologiaChik(rs.getString(148).charAt(0));
					if (rs.getString(149)!=null)
						dato.setGotaGruesa(rs.getString(149).charAt(0));
					if (rs.getString(150)!=null)
						dato.setExtendidoPeriferico(rs.getString(150).charAt(0));
					if (rs.getString(151)!=null)
						dato.setEgo(rs.getString(151).charAt(0));
					if (rs.getString(152)!=null)
						dato.setEgh(rs.getString(152).charAt(0));
					if (rs.getString(153)!=null)
						dato.setCitologiaFecal(rs.getString(153).charAt(0));
					if (rs.getString(154)!=null)
						dato.setFactorReumatoideo(rs.getString(154).charAt(0));
					if (rs.getString(155)!=null)
						dato.setAlbumina(rs.getString(155).charAt(0));
					if (rs.getString(156)!=null)
						dato.setAstAlt(rs.getString(156).charAt(0));
					if (rs.getString(157)!=null)
						dato.setBilirrubinas(rs.getString(157).charAt(0));
					if (rs.getString(158)!=null)
						dato.setCpk(rs.getString(158).charAt(0));
					if (rs.getString(159)!=null)
						dato.setColesterol(rs.getString(159).charAt(0));
					if (rs.getString(160)!=null)
						dato.setInfluenza(rs.getString(160).charAt(0));
					if (rs.getString(161)!=null)
						dato.setOtroExamenLab(rs.getString(161));
					dato.setNumOrdenLaboratorio(rs.getInt(162));
					if (rs.getString(163)!=null)
						dato.setAcetaminofen(rs.getString(163).charAt(0));
					if (rs.getString(164)!=null)
						dato.setAsa(rs.getString(164).charAt(0));
					if (rs.getString(165)!=null)
						dato.setIbuprofen(rs.getString(165).charAt(0));
					if (rs.getString(166)!=null)
						dato.setPenicilina(rs.getString(166).charAt(0));
					if (rs.getString(167)!=null)
						dato.setAmoxicilina(rs.getString(167).charAt(0));
					if (rs.getString(168)!=null)
						dato.setDicloxacilina(rs.getString(168).charAt(0));
					if (rs.getString(169)!=null)
						dato.setOtroAntibiotico(rs.getString(169));
					if (rs.getString(170)!=null)
						dato.setFurazolidona(rs.getString(170).charAt(0));
					if (rs.getString(171)!=null)
						dato.setMetronidazolTinidazol(rs.getString(171).charAt(0));
					if (rs.getString(172)!=null)
						dato.setAlbendazolMebendazol(rs.getString(172).charAt(0));
					if (rs.getString(173)!=null)
						dato.setSulfatoFerroso(rs.getString(173).charAt(0));
					if (rs.getString(174)!=null)
						dato.setSueroOral(rs.getString(174).charAt(0));
					if (rs.getString(175)!=null)
						dato.setSulfatoZinc(rs.getString(175).charAt(0));
					if (rs.getString(176)!=null)
						dato.setLiquidosIv(rs.getString(176).charAt(0));
					if (rs.getString(177)!=null)
						dato.setPrednisona(rs.getString(177).charAt(0));
					if (rs.getString(178)!=null)
						dato.setHidrocortisonaIv(rs.getString(178).charAt(0));
					if (rs.getString(179)!=null)
						dato.setSalbutamol(rs.getString(179).charAt(0));
					if (rs.getString(180)!=null)
						dato.setOseltamivir(rs.getString(180).charAt(0));
					dato.setDiagnostico1(rs.getShort(181));
					dato.setDiagnostico2(rs.getShort(182));
					dato.setDiagnostico3(rs.getShort(183));
					dato.setDiagnostico4(rs.getShort(184));
					if (rs.getString(185)!=null)
						dato.setOtroDiagnostico(rs.getString(185));
					if (rs.getDate(186)!=null)
						dato.setProximaCita(rs.getDate(186));
					if (rs.getString(187)!=null)
						dato.setAmPmUltDiaFiebre(rs.getString(187));
					if (rs.getString(188)!=null)
						dato.setHoraUltDosisAntipiretico(UtilDate.StringToDate(rs.getString(188),"dd/MM/yyyy hh:mm:ss"));
					if (rs.getString(189)!=null)
						dato.setAmPmUltDosisAntipiretico(rs.getString(189));
					if (rs.getDate(190)!=null)
						dato.setFis(rs.getDate(190));
					if (rs.getDate(191)!=null)
						dato.setFif(rs.getDate(191));
					if (rs.getBigDecimal(192)!=null)
						dato.setHepatomegaliaCm(rs.getBigDecimal(192));
					if (rs.getString(193)!=null)
						dato.setEritrocitos(rs.getString(193).charAt(0));
					if (rs.getDate(194)!=null)
						dato.setFechaLinfocitos(rs.getDate(194));
					if (rs.getDate(195)!=null)
						dato.setFechaCierre(rs.getDate(195));
					if (rs.getDate(196)!=null)
						dato.setFechaCierreCambioTurno(rs.getDate(196));
					dato.setUsuarioMedico(rs.getShort(197));
					dato.setUsuarioEnfermeria(rs.getShort(198));					
					if (rs.getString(199)!=null)
						dato.setTurno(rs.getString(199).charAt(0));
					if (rs.getString(200)!=null)
						dato.setHorarioClases(rs.getString(200));
					if (rs.getString(201)!=null)
						dato.setOtro(rs.getString(201).charAt(0));
					dato.setPad(rs.getShort(202));
					dato.setPas(rs.getShort(203));
					dato.setTelef(rs.getLong(204));
					if (rs.getString(205)!=null)
						dato.setHemoconc(rs.getString(205).charAt(0));
					dato.setVomito12h(rs.getShort(206));
					if (rs.getString(207)!=null)
						dato.setOel(rs.getString(207).charAt(0));
					if (rs.getString(208)!=null)
						dato.setHora(rs.getString(208));
					if (rs.getString(209)!=null)
						dato.setHorasv(rs.getString(209));
				
				resultado.add(dato);
			}
		} catch (Exception e) {
		 		e.printStackTrace();
		 		throw new Exception(e);
		} finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
    			logger.error(" No se pudo cerrar conexión ", ex);
            }
        }
        return resultado;
	}

	public void updateHojaConsulta(HojaConsulta hoja) throws Exception{
		PreparedStatement pst = null;
        try {
        	conn = getConection();
            pst = conn.prepareStatement("UPDATE hoja_consulta SET estado_carga = '1' where sec_hoja_consulta = ?");
            pst.setInt(1, hoja.getSecHojaConsulta());
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Se ha producido un error al guardar el registro :: hoja_consulta" + "\n" + e.getMessage(),e);
            throw new Exception(e);
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException ex) {
    			logger.error(" No se pudo cerrar conexión ", ex);
            }
        }
	}
}
