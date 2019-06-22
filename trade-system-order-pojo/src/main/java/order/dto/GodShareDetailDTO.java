package order.dto;

import lombok.Data;

/**
 * Created by tiankun on 2018/1/18.
 */
@Data
public class GodShareDetailDTO {

    private ItemDetailDTO itemdetail;

    private GodDetailDTO godDetail;

    private ResultDTO result;

    private GamesProjectDTO gamesProject;

}
