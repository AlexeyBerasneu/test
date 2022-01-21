package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAll() {
        return playerRepository.findAll();
    }

    public void createPlayer(Player player)
    {
        playerRepository.save(player);
    }

    public void delete(Long id)
    {
        playerRepository.deleteById(id);
    }

    public Player findById(long id)
    {
       return playerRepository.findById(id);
    }

    public boolean existById(long id)
    {
       return playerRepository.existsById(id);
    }

    public Player update(Player player, Long id)
    {
        Optional<Player> updatePlayer = playerRepository.findById(id);
        if (updatePlayer.isPresent())
        {
           Player updatePlayernew=updatePlayer.get();

           if (isValidString(player.getName())) {
               updatePlayernew.setName(player.getName());
           }

           if (isValidString(player.getTitle())) {
               updatePlayernew.setTitle(player.getTitle());

           }


           if (player.getRace()!=null) {
               updatePlayernew.setRace(player.getRace());
           }

            if (player.getProfession()!=null) {
               updatePlayernew.setProfession(player.getProfession());
           }

            if (player.getExperience()!=null) {
               updatePlayernew.setExperience(player.getExperience());
                Integer level = (int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
                updatePlayernew.setLevel(level);
               Integer experienceOut = 50 * (level + 1) * (level + 2) - player.getExperience();
               updatePlayernew.setUntilNextLevel(experienceOut);
               updatePlayernew.setExperience(player.getExperience());
           }


             if (player.getBirthday()!=null) {
               updatePlayernew.setBirthday(player.getBirthday());
           }


             if (player.getBanned()!=null) {
               updatePlayernew.setBanned(player.getBanned());
           }
            playerRepository.save(updatePlayernew);
             return updatePlayernew;

        }
        return null;
    }

    public List<Player> getByFilter(String name, String title, Race race, Profession profession, Long after, Long before,
                                    Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                    Integer maxLevel, PlayerOrder order, Integer pageNumber,Integer pageSize)
    {
        if (order==null)
        {
            order=PlayerOrder.ID;
        }
        if (pageNumber==null)
        {
            pageNumber=0;
        }
        if (pageSize==null)
        {
            pageSize=3;
        }

        Sort sort = Sort.by(Sort.Direction.ASC, order.getFieldName());
        List<Player> players= playerRepository.findAll(sort).stream()
                .filter(player -> name == null || player.getName().contains(name))
                .filter(player -> title == null || player.getTitle().contains(title))
                .filter(player -> race == null || player.getRace().equals(race))
                .filter(player -> profession == null || player.getProfession().equals(profession))
                .filter(player -> after == null || player.getBirthday().getTime() > after )
                .filter(player -> before == null || player.getBirthday().getTime() < before)
                .filter(player -> banned == null || player.getBanned().equals(banned))
                .filter(player -> minExperience == null || player.getExperience() >= minExperience)
                .filter(player -> maxExperience == null || player.getExperience() <= maxExperience)
                .filter(player -> minLevel == null || player.getLevel() >= minLevel)
                .filter(player -> maxLevel == null || player.getLevel() <= maxLevel)
                .collect(Collectors.toList());
        List<Player> playerList= players.stream().skip(pageNumber*pageSize).limit(pageSize).collect(Collectors.toList());
        return playerList;
    }

    public Integer getCount(String name, String title, Race race, Profession profession, Long after, Long before,
                                    Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                    Integer maxLevel)
    {

        return playerRepository.findAll().stream()
                .filter(player -> name == null || player.getName().contains(name))
                .filter(player -> title == null || player.getTitle().contains(title))
                .filter(player -> race == null || player.getRace().equals(race))
                .filter(player -> profession == null || player.getProfession().equals(profession))
                .filter(player -> after == null || player.getBirthday().getTime() > after )
                .filter(player -> before == null || player.getBirthday().getTime() < before)
                .filter(player -> banned == null || player.getBanned().equals(banned))
                .filter(player -> minExperience == null || player.getExperience() >= minExperience)
                .filter(player -> maxExperience == null || player.getExperience() <= maxExperience)
                .filter(player -> minLevel == null || player.getLevel() >= minLevel)
                .filter(player -> maxLevel == null || player.getLevel() <= maxLevel)
                .collect(Collectors.toList()).size();
    }

    private Boolean isValidString(String str)
    {
        return (str!=null&&!str.isEmpty());
    }

}
