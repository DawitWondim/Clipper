package com.clipper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.clipper.dto.PostDTO;
import com.clipper.model.Post;
import com.clipper.model.PostImage;
import com.clipper.service.PostImageService;
import com.clipper.service.PostService;
import com.clipper.service.UserService;
import com.clipper.util.S3Uploader;
import com.clipper.util.Utilities;

@Controller
@CrossOrigin
public class PostController {

	private PostService ps;
	
	@Autowired
	private UserService us;
	
	@Autowired
	private PostImageService pi;

	public PostService getPs() {
		return ps;
	}

	@Autowired
	public void setPs(PostService ps) {
		this.ps = ps;
	}
	
	/**
	 * Retrieve a list of all posts in the system.
	 * @return The list of all posts
	 */
	@GetMapping("/allPosts.json")
	public @ResponseBody List<Post> getAllPosts(){
		return ps.findAll();
	}
	
	/**
	 * Retrieve a JSON representation of a single post based on the ID.
	 * @param id The ID of the post.
	 * @return The post object
	 */
	@GetMapping("/post/{id}.json")
	public @ResponseBody Post getPost(@PathVariable Integer id) {
		Post p = null;
		try {
			p = ps.findById(id);
			return p;
		}
		catch(Exception e) {
			System.out.println("Could not find post.");
		}
		return p;
	}
	
	/**
	 * Delete a specific post.
	 * @param id The id of the post to be deleted
	 * @return The deleted post
	 */
	@DeleteMapping("/post/{id}.json")
	public @ResponseBody Post deletePost(@PathVariable Integer id) {
		Post p = null;
		try {
			p = ps.deletePost(id);
			Utilities.log("Post with the ID of " +  id + " was deleted.");
			return p;
		}
		catch(Exception e) {
			System.out.println("Could not find post.");
		}
		return p;
	}
	
	/**
	 * Retrieve a certain user's posts.
	 * @param id The ID of the user.
	 * @return The list of posts
	 */
	@GetMapping("/user/{id}/posts.json")
	public @ResponseBody List<Post> getPostsByUser(@PathVariable Integer id) {
		List<Post> list = null;
		try {
			list = ps.findAllPostByUserId(id);
			return list;
		}
		catch(Exception e) {
			System.out.println("Could not find specific post.");
		}
		return list;
	}
	/**
	 * Update a user's specific post.
	 * @param p The post
	 * @param id The user's id
	 * @param pId The post id
	 * @return The modified post
	 */
	@PutMapping("/user/{id}/posts/{pId}.json")
	public @ResponseBody Post updatePost(@RequestBody Post p, @PathVariable Integer id, @PathVariable Integer pId) {
		try {
			 Post p1 = ps.updatePost(p);
			 return p1;
		}
		catch(Exception e) {
			System.out.println("Could not update post.");
		}
		return null;
	}
	
	
	/**
	 * Create a new post, given the user id and content.
	 * @param pd The content and user id
	 * @return The newly created post
	 */
	@PostMapping("/addPost.json")
	public @ResponseBody Post addPost(@RequestBody PostDTO pd){
		Post p = null;
		try {
			p = ps.createPost(new Post(0, pd.getContent(), us.getUserById(pd.getUser_id()), null , null));
			pi.addPostImage(new PostImage(0, pd.getLinkOfPic(),p ));
			Post finalPost = ps.findById(p.getId());
			Utilities.log("Post with an ID of " + p.getId() +  " was created.");
			return finalPost;
		}
		catch(Exception e) {
			System.out.println("Could not add post.");
		}
		return p;
	}
	
  @RequestMapping(value = "/testImageReceipt.json", method = RequestMethod.POST, headers={"content-type=multipart/form-data"})
  public @ResponseBody String trialGetImageLink(@RequestParam("imageFile") CommonsMultipartFile file) {
      
      byte[] bytes = file.getBytes();
      String random = S3Uploader.upload(bytes);
      
      return "https://clipperrev.s3.amazonaws.com/Images/" + random;
  }

}