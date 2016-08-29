package com.commit451.easycallback;


import com.google.gson.annotations.SerializedName;

/**
 * The parsed version of a GitHub error body
 */
public class GitHubErrorBody {

    public String message;
    @SerializedName("documentation_url")
    public String documentationUrl;
}
