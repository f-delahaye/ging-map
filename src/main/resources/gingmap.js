function TopicMap () {

	var topics = [];
	
	getTopics: function() {
		return this.topics;
	};

	addTopic: function(topic) {
		this.topics.push(topic);  
	};
}

function Topic() {
	var subjectIdentifers = [];
	
	getSubjectIdentifiers: function() {
		return this.subjectIdentifiers;
	}
	
	addSubjectIdentifier: function(identifier) {
		this.subjectIdentifiers.push(identifier);
	}
}