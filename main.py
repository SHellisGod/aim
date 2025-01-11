from email_fetcher import fetch_emails
from emotion_analyzer import analyze_emotion
from data_manager import organize_emails

def main():
    emails = fetch_emails()
    analyzed_emails = [
        {**email, "emotions": analyze_emotion(email['content'])} 
        for email in emails
    ]
    organized_emails = organize_emails(analyzed_emails)
    print("Emails organized by emotion:", organized_emails)

if __name__ == "__main__":
    main()
