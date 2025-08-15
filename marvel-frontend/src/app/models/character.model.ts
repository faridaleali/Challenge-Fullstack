export interface Thumbnail {
  path: string;
  extension: string;
}

export interface UrlList {
  type: string;
  url: string;
}

export interface ComicSummary {
  resourceURI: string;
  name: string;
}

export interface ComicList {
  available: number;
  collectionURI: string;
  items: ComicSummary[];
  returned: number;
}

export interface SeriesSummary {
  resourceURI: string;
  name: string;
}

export interface SeriesList {
  available: number;
  collectionURI: string;
  items: SeriesSummary[];
  returned: number;
}

export interface StorySummary {
  resourceURI: string;
  name: string;
  type: string;
}

export interface StoryList {
  available: number;
  collectionURI: string;
  items: StorySummary[];
  returned: number;
}

export interface EventSummary {
  resourceURI: string;
  name: string;
}

export interface EventList {
  available: number;
  collectionURI: string;
  items: EventSummary[];
  returned: number;
}

export interface Character {
  id: number;
  name: string;
  description: string;
  modified: string;
  thumbnail: Thumbnail;
  resourceURI: string;
  comics: ComicList;
  series: SeriesList;
  stories: StoryList;
  events: EventList;
  urls: UrlList[];
}

export interface CharacterResponse {
  id: number;
  name: string;
  description: string;
  thumbnailUrl: string;
  comics: string[];
  series: string[];
  stories: string[];
  events: string[];
  urls: string[] | null;
}