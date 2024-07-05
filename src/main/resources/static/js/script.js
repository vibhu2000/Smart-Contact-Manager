console.log("js is enables");

const toggleSidebar = () => {
	if($(".sidebar").is(":visible")) {
		//true
		//band krna hai
		$(".sidebar").css("display", "none")
		$(".content").css("margin-left", "0%")
	} else {
		//false
		//show krna hai
		$(".sidebar").css("display", "block")
		$(".content").css("margin-left", "20%")
	}
}

const search = () => {
	//console.log("searching...")
	
	let query = $("#search-input").val();
	
	if(query==''){
		$(".search-result").hide();
	}else{
		//search
		console.log(query);
		
		//sending request to server
		 let url = `http://localhost:8080/search/${query}`;
		 
		 fetch(url)
		 	.then((response) => {
				return response.json();
			})
			.then((data) => {
				//data
				//console.log(data);
				
				let text = `<div class='list-group'>`;
				
				
				data.forEach((contact) => {
					text += `<a href='/user/contact/${contact.id}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
				});
				
				text += `</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();

			});
		}
	
}