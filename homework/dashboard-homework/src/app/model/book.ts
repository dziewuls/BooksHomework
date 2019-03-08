export interface Book {
    isbn?: string;
    title?: string;
    subtitle?: string;
    publisher?: string;
    publishedDate?: number;
    description?: string;
    pageCount?: number;
    thumbnailUrl?: string;
    language?: string;
    previewLink?: string;
    averageRating?: number;
    authors?: Array<string>;
    categories?: Array<string>;
}
