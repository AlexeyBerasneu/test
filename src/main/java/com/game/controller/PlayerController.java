package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@RestController
public class PlayerController {

   private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    //Get players list
    @GetMapping("/rest/players")

    public ResponseEntity<List<Player>> getPlayers(@RequestParam(value = "name",required = false) String name,
                                   @RequestParam(value = "title",required = false) String title,
                                   @RequestParam(value = "race",required = false) Race race,
                                   @RequestParam(value = "profession",required = false) Profession profession,
                                   @RequestParam(value = "after",required = false) Long after,
                                   @RequestParam(value = "before",required = false) Long before,
                                   @RequestParam(value = "banned",required = false) Boolean banned,
                                   @RequestParam(value = "minExperience",required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience",required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel",required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel",required = false) Integer maxLevel,
                                   @RequestParam(value = "order",required = false) PlayerOrder order,
                                   @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                   @RequestParam(value = "pageSize",required = false) Integer pageSize
                                   ) {

        return new ResponseEntity<>(
                playerService.getByFilter(name,title,race,profession,after,
                before,banned,minExperience,maxExperience,
                minLevel,maxLevel,order,pageNumber,pageSize),HttpStatus.OK);

    }




    //Get players Count
    @GetMapping( "/rest/players/count")
    public ResponseEntity<Integer> count(@RequestParam(value = "name",required = false) String name,
                                         @RequestParam(value = "title",required = false) String title,
                                         @RequestParam(value = "race",required = false) Race race,
                                         @RequestParam(value = "profession",required = false) Profession profession,
                                         @RequestParam(value = "after",required = false) Long after,
                                         @RequestParam(value = "before",required = false) Long before,
                                         @RequestParam(value = "banned",required = false) Boolean banned,
                                         @RequestParam(value = "minExperience",required = false) Integer minExperience,
                                         @RequestParam(value = "maxExperience",required = false) Integer maxExperience,
                                         @RequestParam(value = "minLevel",required = false) Integer minLevel,
                                         @RequestParam(value = "maxLevel",required = false) Integer maxLevel,
                                         @RequestParam(value = "order",required = false) PlayerOrder order,
                                         @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize",required = false) Integer pageSize
    )

    {
        return new ResponseEntity<>(playerService.getCount(name,title,race,profession,after,before,banned,minExperience,maxExperience,
                minLevel,maxLevel),HttpStatus.OK);
    }


    //Create players
    @PostMapping("/rest/players")
    public ResponseEntity<Player> create(@RequestBody Player player) {
        if (    player.getName() == null ||
                player.getName().length()>12||
                player.getName().equals("")||
                player.getTitle() == null ||
                player.getTitle().length()>30||
                player.getRace() == null ||
                player.getProfession() == null ||
                player.getBirthday() == null ||
                player.getBirthday().getTime()<0||
                player.getBirthday().getYear()<100||
                player.getBirthday().getYear()>1100||
                player.getExperience() == null||
                player.getExperience()<0||
                player.getExperience()>10000000
        ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Player newplayer = new Player();
            Integer level = (int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
            Integer experienceOut = 50 * (level + 1) * (level + 2) - player.getExperience();
            newplayer.setName(player.getName());
            newplayer.setTitle(player.getTitle());
            newplayer.setRace(player.getRace());
            newplayer.setProfession(player.getProfession());
            newplayer.setBirthday(player.getBirthday());
            newplayer.setBanned(player.getBanned());
            newplayer.setExperience(player.getExperience());
            newplayer.setLevel(level);
            newplayer.setUntilNextLevel(experienceOut);
            playerService.createPlayer(newplayer);
            return new ResponseEntity<>(newplayer, HttpStatus.OK);
        }
    }



    //Get player
    @GetMapping("/rest/players/{id}")
    public ResponseEntity <Player>  getPlayer(@PathVariable("id") long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (playerService.existById(id))
        {

            return new ResponseEntity(playerService.findById(id), HttpStatus.OK);
        }
        else {
            return new ResponseEntity( HttpStatus.NOT_FOUND);
        }
    }


    //Update player
    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player player,@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //Если тело запроса пустое
        if (player.getName()==null&&
            player.getTitle()==null&&
            player.getRace()==null&&
            player.getProfession()==null&&
            player.getExperience()==null&&
            player.getLevel()==null&&
            player.getUntilNextLevel()==null&&
            player.getBirthday()==null&&
            player.getBanned()==null)
        {
            Player notUpdate = playerService.findById(id);
            return new ResponseEntity<>(notUpdate,HttpStatus.OK);
        }
        if (playerService.existById(id))
        {
            if (isPlayerParamsValid(player))
            {
                return new ResponseEntity(playerService.update(player,id), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity( HttpStatus.NOT_FOUND);
        }
    }


    //Delete player
    @DeleteMapping( "/rest/players/{id}")
    public ResponseEntity<Void> deletePlayer (@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (playerService.existById(id))
        {
            playerService.delete(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity( HttpStatus.NOT_FOUND);
        }
    }

    private Boolean isPlayerParamsValid (Player player)
    {
        if (player.getName()!=null&&player.getName().length()>12)
        {
            return false;
        }
        if (player.getTitle()!=null&&player.getTitle().length()>30)
        {
            return false;
        }
        if (player.getExperience()!=null&&(player.getExperience()<0||player.getExperience()>10000000))
        {
            return false;
        }
        if (player.getBirthday()!=null&&(player.getBirthday().getYear()<100||player.getBirthday().getYear()>1100))
        {
            return false;
        }
        return true;
    }


}
