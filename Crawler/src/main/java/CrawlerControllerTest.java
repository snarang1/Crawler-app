package main.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)

@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CrawlerControllerTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	/** test to verify if a user doen't enters any url.
	 * 
	 */
	@Test
	public void testNoInput() {
		try {

			assertTrue(this.mockMvc.perform(post("/getInfo").content(" ")).andExpect(status().isOk()).andReturn()
					.getResponse().getContentAsString().contains("Invalid Url"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * test to verify if a user enter incorrect url.
	 * 
	 */
	@Test
	public void testInvalidInput() {
		try {

			assertTrue(this.mockMvc.perform(post("/getInfo").content("abc.com")).andExpect(status().isOk()).andReturn()
					.getResponse().getContentAsString().contains("Invalid Url"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * test to verify when a user enters a correct url it should have either one of
	 * these assertion statements true. For any case a valid url will have it's own
	 * web address as internal address
	 * 
	 */
	@Test
	public void testValidInput() {
		try {

			String result = this.mockMvc.perform(post("/getInfo").content("https://www.wipro.com"))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			assertTrue(result.length() > 0);
			assertTrue(
					result.contains("Internal Links") || result.contains("outside Links") || result.contains("files"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
