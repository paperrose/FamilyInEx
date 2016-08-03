package com.artfonapps.familyinex;


import com.artfonapps.familyinex.db.models.User;

/**
 * Created by paperrose on 02.06.2016.
 */
public class ApplicationParameters {
    public static String spreadsheetID = "1N_3K_vhrBSI-k-tBYTV-1UbkIcAYrqIO9jZrN8JYjl4";
    //public static String[] SCOPES = { SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE };


    public static String GOOGLE_API_SERVER = "https://fcm.googleapis.com";
    public static String GOOGLE_API_URI = "/fcm/send";
    public static String GOOGLE_API_KEY = "AIzaSyBjmmAgrb7qckxnUtubirwfkHf4WmOVDb8";

    public static User currentUser = null;
    public static int timerInterval = 3600000;



    /*
    *         $data = new stdClass;

        $data->date = @strtotime($this->created)*1000;
        $data->title = $this->title;
        $data->description = $this->text;

        $post = array(
            "registration_ids"  => [registration_ids],
            "data"              => {date:date, title:title, description:description},
        );

        $vars = json_encode($post);
        $content = $vars;

        fwrite($fp, "POST ".self::API_URI." HTTP/1.1\r\n");
        fwrite($fp, "Host: ".self::API_SERVER."\r\n");
        fwrite($fp, "Content-Type: application/json\r\n");
        fwrite($fp, "Authorization: key=" . self::API_KEY . "\r\n");
        fwrite($fp, "Content-Length: ".strlen($content)."\r\n");
        fwrite($fp, "Connection: close\r\n");
        fwrite($fp, "\r\n");



    * */

}
