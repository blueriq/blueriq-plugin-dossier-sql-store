package com.aquima.plugin.dossier.repository;

import com.aquima.plugin.dossier.model.Dossier;

import java.util.List;
import java.util.Map;

public interface IDossierRepository/* extends JpaRepository<Dossier, Long>, JpaSpecificationExecutor<Dossier> */
{

  List<Dossier> findByType(String type);

  List<Dossier> find(String dossierType, Map<String, Object> searchValues);

  Dossier save(Dossier dossier);

  Dossier findOne(Long id);

  void delete(Dossier dossier);

  void delete(Long id);

  List<Dossier> findAll();

}
